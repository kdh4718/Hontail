package com.hontail.back.bartender.service;

import com.hontail.back.bartender.dto.ChatMessageDto;
import com.hontail.back.bartender.dto.ChatResponseDto;
import com.hontail.back.bartender.dto.CocktailDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BartenderServiceImpl implements BartenderService {
    // 필요한 의존성, 상수들
    private final CocktailRepository cocktailRepository;
    private final OpenAiService openAiService;
    private final Map<Integer, List<ChatMessageDto>> chatHistory = new HashMap<>();    // 사용자별 채팅 기록
    private final List<Integer> recentRecommendations = new ArrayList<>();           // 최근 추천 기록
    private static final int MAX_HISTORY_SIZE = 5;                                   // 최근 추천 기록 최대 크기

    // 응답 마커 상수
    private static final String RECOMMENDATION_MARKER = "###RECOMMENDATION###";
    private static final String NO_RECOMMENDATION_MARKER = "###NO_RECOMMENDATION###";

    // 로그인 여부에 따른 호칭 설정 -> 비로그인 : 손님 / 로그인 : 사용자 닉네임
    private String getEffectiveNickname(Integer userId, String nickname) {
        if (userId != null && nickname != null) {
            return nickname + "님"; // 로그인 유저
        }
        return "손님";  // 비로그인 유저
    }
    // 사용자 첫 입장 시 초기 인사말 생성
    @Override
    public ChatResponseDto getInitialGreeting(Integer userId, String nickname) {
        String effectiveNickname = getEffectiveNickname(userId, nickname);
        String greeting = generateTimeBasedGreeting(effectiveNickname);

        // 로그인한 사용자만 채팅 기록 저장
        if (userId != null) {
            List<ChatMessageDto> history = chatHistory.computeIfAbsent(userId, k -> new ArrayList<>());
            if (history.size() >= MAX_HISTORY_SIZE) {
                throw new CustomException(ErrorCode.BARTENDER_MAX_HISTORY_EXCEEDED);
            }
            history.add(new ChatMessageDto("assistant", greeting));
        }

        return new ChatResponseDto(greeting, false, null);
    }

    // 시간대별 맞춤 인사말 생성 (OpenAI 활용)
    private String generateTimeBasedGreeting(String nickname) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 0 || hour > 23) {
            throw new CustomException(ErrorCode.BARTENDER_INVALID_TIME);
        }
        String timeContext = getTimeContext(hour);

        String systemPrompt = String.format(
                "당신은 칵테일 바의 따뜻하고 친근한 바텐더입니다. %s 시간대에 방문한 %s을(를) 위한 첫 인사말을 작성해주세요.\n" +
                "다음 형식 중 하나를 랜덤하게 선택하여 자연스럽게 변형해 작성해주세요:\n" +
                "1. 안녕하세요, %s! [시간대 인사] 지금 기분이 어떠신지 들려주시면 제가 어울리는 칵테일 추천해드릴게요.\n" +
                "2. 어서오세요, %s! [시간대 인사] 오늘 있었던 일을 들려주시면 제가 딱 맞는 칵테일을 골라드릴게요.\n" +
                "3. 환영합니다, %s! [시간대 인사] 지금 기분이나 상황을 말씀해주시면 특별한 칵테일을 추천해드릴게요.\n\n" +
                "시간대별 인사말 예시:\n" +
                "새벽(03-07시):\n" +
                "- 이른 시간인데도 찾아주셨네요\n" +
                "- 고요한 새벽에 찾아주셨네요\n" +
                "- 아직 어두운 시간이네요\n" +
                "- 새벽의 특별한 손님이네요\n\n" +
                "아침(07-11시):\n" +
                "- 상쾌한 아침이네요\n" +
                "- 새로운 하루가 시작됐어요\n" +
                "- 활기찬 아침이에요\n" +
                "- 아침부터 찾아주셨네요\n" +
                "- 하루의 시작을 함께하게 되어 기쁘네요\n" +
                "- 아침의 첫 손님이네요\n\n" +
                "점심(11-14시):\n" +
                "- 점심 시간에 찾아주셨네요\n" +
                "- 한낮의 여유로운 시간이네요\n" +
                "- 점심 식사는 하셨나요\n" +
                "- 햇살 가득한 시간이네요\n" +
                "- 점심시간의 특별한 휴식이네요\n" +
                "- 한낮의 달콤한 시간이에요\n\n" +
                "오후(14-17시):\n" +
                "- 여유로운 오후네요\n" +
                "- 달콤한 오후를 보내고 계시네요\n" +
                "- 오후의 휴식 시간이네요\n" +
                "- 포근한 오후예요\n" +
                "- 오후의 특별한 시간이네요\n" +
                "- 한가로운 오후를 보내고 계시네요\n\n" +
                "저녁(17-21시):\n" +
                "- 하루의 마무리 시간이네요\n" +
                "- 편안한 저녁 시간이네요\n" +
                "- 바쁜 하루를 보내셨죠\n" +
                "- 저녁 노을이 아름다운 시간이에요\n" +
                "- 퇴근 후의 여유를 즐기실 시간이네요\n" +
                "- 하루의 피로를 풀기 좋은 시간이에요\n\n" +
                "밤(21-03시):\n" +
                "- 특별한 밤이 될 것 같네요\n" +
                "- 로맨틱한 밤이네요\n" +
                "- 조용한 밤이에요\n" +
                "- 늦은 시간까지 수고 많으셨어요\n" +
                "- 밤의 분위기가 좋네요\n" +
                "- 오늘 하루를 마무리하기 좋은 시간이에요\n\n" +
                "규칙:\n" +
                "1. 반드시 칵테일 추천 가능성을 언급할 것\n" +
                "2. 자연스러운 한국어 톤 유지\n" +
                "3. 형식적이거나 딱딱한 표현 피하기\n" +
                "4. 시간대에 맞는 분위기 반영하기\n" +
                "5. 제시된 예시들을 조합하거나 자연스럽게 변형 가능",
                timeContext,
                nickname,
                nickname,
                nickname,
                nickname
        );

        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("system", systemPrompt));
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", "첫 인사말 생성해주세요."));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(150)
                .temperature(0.7)
                .build();

        try {
            String greeting = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent().trim();

            if (greeting == null || greeting.trim().isEmpty()) {
                throw new CustomException(ErrorCode.BARTENDER_OPENAI_ERROR);
            }

            if (nickname.equals("손님")) {
                greeting += " 추가로 로그인하셔서 저를 이용하신다면, 대화 내역을 토대로 더 개인화된 추천을 받으실 수 있어요!";
            }

            return greeting;
        } catch (Exception e) {
            log.error("인사말 생성 중 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.BARTENDER_OPENAI_ERROR);
        }
    }

    // 현재 시간에 따른 시간대 컨텍스트 반환
    private String getTimeContext(int hour) {
        if (hour >= 3 && hour < 7) return "새벽";
        else if (hour >= 7 && hour < 11) return "아침";
        else if (hour >= 11 && hour < 14) return "점심";
        else if (hour >= 14 && hour < 17) return "오후";
        else if (hour >= 17 && hour < 21) return "저녁";
        else return "밤";
    }

    // 랜덤 칵테일 선택 및 최근 추천 기록 관리
    private Cocktail findRandomCocktail() {
        Cocktail cocktail = cocktailRepository.findRandomCocktail()
                .orElseThrow(() -> new CustomException(ErrorCode.BARTENDER_COCKTAIL_NOT_FOUND));

        recentRecommendations.add(cocktail.getId());
        if (recentRecommendations.size() > MAX_HISTORY_SIZE) {
            recentRecommendations.remove(0);
        }

        return cocktail;
    }

    // 사용자 메시지에 대한 응답 생성 및 칵테일 추천
    @Override
    public ChatResponseDto chat(String userMessage, Integer userId, String nickname) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        String effectiveNickname = getEffectiveNickname(userId, nickname);

        // 로그인한 사용자만 채팅 기록 저장
        if (userId != null) {
            List<ChatMessageDto> history = chatHistory.computeIfAbsent(userId, k -> new ArrayList<>());
            history.add(new ChatMessageDto("user", userMessage));
        }

        Cocktail cocktail = findRandomCocktail();

        // OpenAI API 요청 메시지 구성
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();

        String systemPrompt = String.format(
                "당신은 공감 능력이 뛰어난 칵테일 전문 바텐더입니다.\n\n" +
                "1. 기본 정보:\n" +
                "   - 추천할 수 있는 유일한 칵테일: %s (도수: %d%%)\n" +
                "   - 손님 호칭: %s\n\n" +
                "2. 응답 규칙:\n" +
                "   - 자연스러운 대화 흐름 유지\n" +
                "   - 손님의 감정과 경험에 먼저 공감\n" +
                "   - 공감 내용과 연결하여 칵테일 추천\n" +
                "   - 전문적이고 따뜻한 톤 유지\n" +
                "   - 이전 대화 맥락 유지\n" +
                "   - 다른 칵테일 언급하지 않기\n\n" +
                "3. 칵테일 추천 규칙:\n" +
                "   - 추천해야 하는 경우:\n" +
                "     * 손님이 직접적으로 추천을 요청할 때 ('추천해주세요', '다른 거 없나요?', '다른거는요?' 등)\n" +
                "     * 손님이 기분이나 상황을 응답하였을 때\n" +
                "     * 손님이 이전 추천과 다른 새로운 칵테일을 원할 때\n" +
                "   - 추천 시 응답 예시:\n" +
                "     * '네, 오늘은 %s를 추천드립니다. 이 칵테일은...' + RECOMMENDATION\n" +
                "     * '다른 칵테일을 원하신다면, %s는 어떠세요?' + RECOMMENDATION\n" +
                "   - 절대 추천하지 않아야 하는 경우:\n" +
                "     * 이전 추천 목록을 물어볼 때 (단순히 기억하고 있음을 보여주기)\n" +
                "     * 술을 마시지 않겠다는 의사 표현시\n" +
                "     * 단순 감정 표현이나 일상 대화시 (예: 안녕하세요, 감사합니다)\n" +
                "4. 응답 형식:\n" +
                "   - 칵테일 추천 시: 응답 끝에 '%s' 추가\n" +
                "   - 추천하지 않을 시: 응답 끝에 '%s' 추가\n\n" +
                "5. 대화 예시:\n" +
                "   Q: '지금까지 어떤 칵테일을 추천해주셨나요?'\n" +
                "   A: (이전 추천 내역 언급, 취향 파악 시도) + NO_RECOMMENDATION\n\n" +
                "   Q: '오늘 기분이 좋아서 특별한 걸 마시고 싶어요'\n" +
                "   A: (공감 + 칵테일 추천) + RECOMMENDATION\n\n" +
                "   Q: '술은 안 마시고 싶어요'\n" +
                "   A: (이해와 공감 표현) + NO_RECOMMENDATION\n\n" +
                "   Q: '아까 추천해주신 칵테일 다시 한번 설명해주세요'\n" +
                "   A: (해당 칵테일 재설명) + RECOMMENDATION",
                cocktail.getCocktailName(),
                cocktail.getAlcoholContent(),
                effectiveNickname,
                cocktail.getCocktailName(),
                cocktail.getCocktailName(),
                RECOMMENDATION_MARKER,
                NO_RECOMMENDATION_MARKER
        );

        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("system", systemPrompt));

        // 로그인한 사용자의 경우에만 이전 대화 기록 포함
        if (userId != null) {
            List<ChatMessageDto> history = chatHistory.get(userId);
            if (history != null) {
                int startIndex = Math.max(0, history.size() - 5);
                for (int i = startIndex; i < history.size(); i++) {
                    ChatMessageDto msg = history.get(i);
                    messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                            msg.getRole(), msg.getContent()));
                }
            }
        }

        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", userMessage));

        // OpenAI API 요청 설정
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(400)
                .temperature(0.7)
                .build();

        try {
            int maxRetries = 3;  // 최대 재시도 횟수
            String response = null;
            boolean isValidResponse = false;
            boolean isRecommendation = false;
            String cleanResponse = null;

            for (int attempt = 0; attempt < maxRetries && !isValidResponse; attempt++) {
                // API 호출 및 응답 받기
                response = openAiService.createChatCompletion(request)
                        .getChoices().get(0).getMessage().getContent();

                if (response == null || response.trim().isEmpty()) {
                    if (attempt == maxRetries - 1) {
                        throw new CustomException(ErrorCode.BARTENDER_OPENAI_ERROR);
                    }
                    continue;
                }

                // 응답 처리 및 마커 확인
                isRecommendation = false;
                cleanResponse = response;

                // 마커 확인 및 제거
                if (response.contains(RECOMMENDATION_MARKER)) {
                    isRecommendation = true;
                    cleanResponse = response.replace(RECOMMENDATION_MARKER, "").trim();
                } else if (response.contains(NO_RECOMMENDATION_MARKER)) {
                    isRecommendation = false;
                    cleanResponse = response.replace(NO_RECOMMENDATION_MARKER, "").trim();
                    isValidResponse = true;  // 추천하지 않는 경우는 항상 유효
                    break;
                } else {
                    log.warn("응답에 마커가 없습니다. 응답 분석을 시작합니다.");

                    // 이전 추천 내역 관련 질문 확인
                    boolean isAskingPreviousRecommendations =
                            (userMessage.contains("어떤") || userMessage.contains("무슨") ||
                                    userMessage.contains("뭐") || userMessage.contains("어떻게")) &&
                                    (userMessage.contains("추천") || userMessage.contains("칵테일")) &&
                                    (userMessage.contains("했") || userMessage.contains("주") ||
                                            userMessage.contains("받"));

                    // 술 거절 의사 확인
                    boolean isRejectingAlcohol =
                            userMessage.toLowerCase().contains("안 마실래") ||
                                    userMessage.contains("그만") ||
                                    userMessage.contains("안주세요") ||
                                    userMessage.contains("괜찮아요") ||
                                    userMessage.contains("다음에") ||
                                    userMessage.contains("안 마실");

                    // 새로운 추천 요청 확인 추가
                    boolean isAskingNewRecommendation =
                            userMessage.contains("다른") ||
                                    userMessage.contains("또") ||
                                    userMessage.contains("다음") ||
                                    (userMessage.contains("추천") && userMessage.length() < 10) ||
                                    (userMessage.endsWith("는요?") || userMessage.endsWith("있어요?"));

                    if (isAskingPreviousRecommendations || isRejectingAlcohol) {
                        isRecommendation = false;
                        isValidResponse = true;  // 추천하지 않는 경우는 항상 유효
                        break;
                    } else if (isAskingNewRecommendation && !isRejectingAlcohol) {
                        isRecommendation = true;
                    } else {
                        boolean containsCocktailName = response.contains(cocktail.getCocktailName());
                        boolean hasRecommendationIntent =
                                response.contains("추천") ||
                                        response.contains("권해드리") ||
                                        response.contains("어떠세요") ||
                                        response.contains("마셔보세요") ||
                                        response.contains("준비해드릴게요") ||
                                        response.contains("드시면 좋을 것 같아요") ||
                                        (response.contains(cocktail.getCocktailName()) &&
                                                (response.contains("는") || response.contains("을") || response.contains("를")));

                        boolean hasRejectionIntent =
                                response.contains("죄송") ||
                                        response.contains("다음에") ||
                                        response.contains("안마시") ||
                                        response.contains("힘들") ||
                                        response.contains("괜찮으세요");

                        isRecommendation = containsCocktailName &&
                                hasRecommendationIntent &&
                                !hasRejectionIntent;
                    }
                }

                // 추천인 경우에만 칵테일 이름 검증
                if (isRecommendation) {
                    if (response.contains(cocktail.getCocktailName())) {
                        isValidResponse = true;
                    } else {
                        log.warn("응답이 선택된 칵테일과 일치하지 않습니다. 재시도 #{}", attempt + 1);
                        continue;
                    }
                } else {
                    isValidResponse = true;  // 추천이 아닌 경우는 항상 유효
                }

                // 추천일 경우 안내 문구 추가
                if (isRecommendation) {
                    boolean hasClickGuide =
                            cleanResponse.contains("이미지를 클릭") ||
                                    cleanResponse.contains("상세 정보");

                    if (!hasClickGuide) {
                        if (response.contains("다시") || response.contains("재")) {
                            cleanResponse += " 칵테일 상세 정보는 이미지를 클릭하시면 확인하실 수 있어요!";
                        } else {
                            cleanResponse += " 말씀드린 칵테일이 궁금하시다면 이미지를 클릭해서 자세히 살펴보실 수 있어요!";
                        }
                    }
                }

                // 응답 길이 제한
                if (cleanResponse.length() > 1000) {
                    if (attempt == maxRetries - 1) {
                        throw new CustomException(ErrorCode.BARTENDER_RESPONSE_TOO_LONG);
                    }
                    continue;
                }
            }

            // 모든 재시도가 실패한 경우
            if (!isValidResponse) {
                throw new CustomException(ErrorCode.BARTENDER_OPENAI_ERROR);
            }

            // 로그인한 사용자만 채팅 기록 저장
            if (userId != null) {
                List<ChatMessageDto> history = chatHistory.get(userId);
                if (history == null) {
                    throw new CustomException(ErrorCode.BARTENDER_CHAT_NOT_FOUND);
                }
                history.add(new ChatMessageDto("assistant", cleanResponse));
            }

            // 칵테일 추천 시 DTO 생성
            CocktailDto cocktailDto = isRecommendation ? new CocktailDto(
                    cocktail.getId(),
                    cocktail.getCocktailName(),
                    cocktail.getCocktailDescription(),
                    cocktail.getImageUrl(),
                    cocktail.getAlcoholContent()
            ) : null;

            return new ChatResponseDto(cleanResponse, isRecommendation, cocktailDto);

        } catch (Exception e) {
            log.error("Error in chat process: ", e);
            throw new CustomException(ErrorCode.BARTENDER_OPENAI_ERROR);
        }
    }
}