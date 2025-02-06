package com.hontail.back.bartender.service;

import com.hontail.back.bartender.dto.ChatMessageDto;
import com.hontail.back.bartender.dto.ChatResponseDto;
import com.hontail.back.bartender.dto.CocktailDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.repository.CocktailRepository;
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
    private final Map<String, List<ChatMessageDto>> chatHistory = new HashMap<>();    // 사용자별 채팅 기록
    private final List<Integer> recentRecommendations = new ArrayList<>();           // 최근 추천 기록
    private static final int MAX_HISTORY_SIZE = 3;                                   // 최근 추천 기록 최대 크기

    // 응답 마커 상수
    private static final String RECOMMENDATION_MARKER = "###RECOMMENDATION###";
    private static final String NO_RECOMMENDATION_MARKER = "###NO_RECOMMENDATION###";

    // 로그인 여부에 따른 호칭 설정 -> 비로그인 : 손님 / 로그인 : 사용자 닉네임
    private String getEffectiveNickname(String userId, String nickname) {
        return (userId != null && !userId.trim().isEmpty()) ? nickname : "손";   // 손님
    }

    // 사용자 첫 입장 시 초기 인사말 생성
    @Override
    public ChatResponseDto getInitialGreeting(String userId, String nickname) {
        String effectiveNickname = getEffectiveNickname(userId, nickname);
        String greeting = generateTimeBasedGreeting(effectiveNickname);

        // 로그인한 사용자만 채팅 기록 저장
        if (userId != null && !userId.trim().isEmpty()) {
            List<ChatMessageDto> history = chatHistory.computeIfAbsent(userId, k -> new ArrayList<>());
            history.add(new ChatMessageDto("assistant", greeting));
        }

        return new ChatResponseDto(greeting, false, null);
    }

    // 시간대별 맞춤 인사말 생성 (OpenAI 활용)
    private String generateTimeBasedGreeting(String nickname) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String timeContext = getTimeContext(hour);

        try {
            String systemPrompt = String.format(
                    "당신은 칵테일 바의 따뜻하고 친근한 바텐더입니다. %s 시간대에 방문한 %s님을 위한 첫 인사말을 작성해주세요.\n" +
                            "다음 형식 중 하나를 랜덤하게 선택하여 자연스럽게 변형해 작성해주세요:\n" +
                            "1. '안녕하세요, [닉네임]님! [시간대 인사] 지금 기분이 어떠신지 들려주시면 제가 어울리는 칵테일 추천해드릴게요.'\n" +
                            "2. '어서오세요, [닉네임]님! [시간대 인사] 오늘 있었던 일을 들려주시면 제가 딱 맞는 칵테일을 골라드릴게요.'\n" +
                            "3. '환영합니다, [닉네임]님! [시간대 인사] 지금 기분이나 상황을 말씀해주시면 특별한 칵테일을 추천해드릴게요.'\n\n" +
                            "시간대별 인사말 예시:\n" +
                            "새벽(03-07시):\n" +
                            "- '이른 시간인데도 찾아주셨네요'\n" +
                            "- '고요한 새벽에 찾아주셨네요'\n" +
                            "- '아직 어두운 시간이네요'\n" +
                            "- '새벽의 특별한 손님이네요'\n\n" +
                            "아침(07-11시):\n" +
                            "- '상쾌한 아침이네요'\n" +
                            "- '새로운 하루가 시작됐어요'\n" +
                            "- '활기찬 아침이에요'\n" +
                            "- '아침부터 찾아주셨네요'\n" +
                            "- '하루의 시작을 함께하게 되어 기쁘네요'\n" +
                            "- '아침의 첫 손님이네요'\n\n" +
                            "점심(11-14시):\n" +
                            "- '점심 시간에 찾아주셨네요'\n" +
                            "- '한낮의 여유로운 시간이네요'\n" +
                            "- '점심 식사는 하셨나요'\n" +
                            "- '햇살 가득한 시간이네요'\n" +
                            "- '점심시간의 특별한 휴식이네요'\n" +
                            "- '한낮의 달콤한 시간이에요'\n\n" +
                            "오후(14-17시):\n" +
                            "- '여유로운 오후네요'\n" +
                            "- '달콤한 오후를 보내고 계시네요'\n" +
                            "- '오후의 휴식 시간이네요'\n" +
                            "- '포근한 오후예요'\n" +
                            "- '오후의 특별한 시간이네요'\n" +
                            "- '한가로운 오후를 보내고 계시네요'\n\n" +
                            "저녁(17-21시):\n" +
                            "- '하루의 마무리 시간이네요'\n" +
                            "- '편안한 저녁 시간이네요'\n" +
                            "- '바쁜 하루를 보내셨죠'\n" +
                            "- '저녁 노을이 아름다운 시간이에요'\n" +
                            "- '퇴근 후의 여유를 즐기실 시간이네요'\n" +
                            "- '하루의 피로를 풀기 좋은 시간이에요'\n\n" +
                            "밤(21-03시):\n" +
                            "- '특별한 밤이 될 것 같네요'\n" +
                            "- '로맨틱한 밤이네요'\n" +
                            "- '조용한 밤이에요'\n" +
                            "- '늦은 시간까지 수고 많으셨어요'\n" +
                            "- '밤의 분위기가 좋네요'\n" +
                            "- '오늘 하루를 마무리하기 좋은 시간이에요'\n\n" +
                            "규칙:\n" +
                            "1. 반드시 칵테일 추천 가능성을 언급할 것\n" +
                            "2. 자연스러운 한국어 톤 유지\n" +
                            "3. 형식적이거나 딱딱한 표현 피하기\n" +
                            "4. 시간대에 맞는 분위기 반영하기\n" +
                            "5. 제시된 예시들을 조합하거나 자연스럽게 변형 가능",
                    timeContext, nickname
            );

            // OpenAI API 요청 메시지 구성
            List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage("system", systemPrompt));
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", "첫 인사말 생성해주세요."));
            // API 요청 설정
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .maxTokens(150)
                    .temperature(0.7)
                    .build();

            // API 호출 및 응답 받기
            String greeting = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent().trim();

            return greeting;

        } catch (Exception e) {
            // 오류 발생 시 기본 인사말 반환
            log.error("인사말 생성 중 오류 발생: {}", e.getMessage(), e);
            return String.format("안녕하세요, %s님! 오늘 하루는 어떠신가요?", nickname);
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
        // 랜덤 칵테일 선택
        Cocktail cocktail = cocktailRepository.findRandomCocktail()
                .orElseThrow(() -> new RuntimeException("사용 가능한 칵테일이 없습니다."));

        // 최근 추천 기록 관리
        recentRecommendations.add(cocktail.getId());
        if (recentRecommendations.size() > MAX_HISTORY_SIZE) {
            recentRecommendations.remove(0);
        }

        return cocktail;
    }

    // 사용자 메시지에 대한 응답 생성 및 칵테일 추천
    @Override
    public ChatResponseDto chat(String userMessage, String userId, String nickname) {
        // 입력값 검증
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new ChatResponseDto("메시지를 입력해주세요.", false, null);
        }

        String effectiveNickname = getEffectiveNickname(userId, nickname);

        // 로그인한 사용자만 채팅 기록 저장
        if (userId != null && !userId.trim().isEmpty()) {
            List<ChatMessageDto> history = chatHistory.computeIfAbsent(userId, k -> new ArrayList<>());
            history.add(new ChatMessageDto("user", userMessage));
        }

        try {
            // 랜덤 칵테일 선택
            Cocktail cocktail = findRandomCocktail();
            List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();

            // OpenAI 시스템 프롬프트 설정
            String systemPrompt = String.format(
                    "당신은 공감 능력이 뛰어난 칵테일 전문 바텐더입니다.\n\n" +
                            "1. 기본 정보:\n" +
                            "   - 추천할 수 있는 유일한 칵테일: %s (도수: %d%%)\n" +
                            "   - 손님 호칭: %s님\n\n" +
                            "2. 응답 규칙:\n" +
                            "   - 손님의 감정과 경험에 먼저 공감\n" +
                            "   - 공감 내용과 연결하여 칵테일 추천\n" +
                            "   - 전문적이고 따뜻한 톤 유지\n" +
                            "   - 이전 대화 맥락 유지\n" +
                            "   - 다른 칵테일 언급하지 않기\n\n" +
                            "3. 첫 방문 손님 응답 예시:\n" +
                            "   - '처음 방문하셨네요! 특별한 %s 칵테일로 시작하시는 건 어떠세요?'\n" +
                            "   - '첫 방문을 환영합니다! %s의 매력적인 맛으로 좋은 추억을 만들어보세요.'\n" +
                            "   - '새로운 손님을 맞이하게 되어 기쁩니다. %s와 함께 특별한 시간 보내세요.'\n\n" +
                            "4. 응답 형식:\n" +
                            "   - 칵테일 추천 시: 응답 끝에 '%s' 추가\n" +
                            "   - 추천하지 않을 시: 응답 끝에 '%s' 추가\n\n" +
                            "5. 추천 기준:\n" +
                            "   - 추천 요청 시\n" +
                            "   - 이전 추천 거절 시\n" +
                            "   - 상황이 어울릴 때\n" +
                            "   - 술 자체를 거절할 경우 추천 중단",
                    cocktail.getCocktailName(),
                    cocktail.getAlcoholContent(),
                    effectiveNickname,
                    cocktail.getCocktailName(),
                    cocktail.getCocktailName(),
                    cocktail.getCocktailName(),
                    RECOMMENDATION_MARKER,
                    NO_RECOMMENDATION_MARKER
            );

            messages.add(new com.theokanning.openai.completion.chat.ChatMessage("system", systemPrompt));

            // 로그인한 사용자의 경우에만 이전 대화 기록 포함
            if (userId != null && !userId.trim().isEmpty()) {
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

            // API 호출 및 응답 받기
            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            // 응답 처리 및 마커 확인
            boolean isRecommendation = false;
            String cleanResponse = response;

            if (response.contains(RECOMMENDATION_MARKER)) {
                isRecommendation = true;
                cleanResponse = response.replace(RECOMMENDATION_MARKER, "").trim();
            } else if (response.contains(NO_RECOMMENDATION_MARKER)) {
                isRecommendation = false;
                cleanResponse = response.replace(NO_RECOMMENDATION_MARKER, "").trim();
            } else {
                log.warn("응답에 추천 마커가 없습니다. 응답: {}", response);
                isRecommendation = response.contains(cocktail.getCocktailName()) &&
                        (response.contains("추천") || response.contains("권해드리"));
            }

            // 응답 길이 제한
            if (cleanResponse.length() > 1000) {
                log.warn("응답이 너무 깁니다. 길이: {}", cleanResponse.length());
                cleanResponse = cleanResponse.substring(0, 1000) + "...";
            }

            // 로그인한 사용자만 채팅 기록 저장
            if (userId != null && !userId.trim().isEmpty()) {
                List<ChatMessageDto> history = chatHistory.get(userId);
                if (history != null) {
                    history.add(new ChatMessageDto("assistant", cleanResponse));
                }
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
            // 오류 처리 및 기본 응답 반환
            log.error("OpenAI API 호출 중 오류 발생: {}", e.getMessage(), e);
            return new ChatResponseDto(
                    String.format("죄송합니다, %s님. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", effectiveNickname),
                    false,
                    null
            );
        }
    }
}