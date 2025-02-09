package com.hontail.back.global.exception;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Global Error Codes (공통 에러)
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT(400, "잘못된 입력입니다."),

    // Auth Error Codes (인증 관련)
    INVALID_USER_SESSION(401, "유효하지 않은 사용자 세션입니다."),
    INVALID_NICKNAME(400, "유효하지 않은 닉네임입니다."),

    // Bartender Error Codes (바텐더 도메인)
    BARTENDER_OPENAI_ERROR(500, "AI 바텐더 응답 생성 중 오류가 발생했습니다."),
    BARTENDER_COCKTAIL_NOT_FOUND(404, "칵테일을 찾을 수 없습니다."),
    BARTENDER_CHAT_NOT_FOUND(404, "채팅 기록을 찾을 수 없습니다."),
    BARTENDER_CHAT_SAVE_ERROR(500, "채팅 기록 저장 중 오류가 발생했습니다."),
    BARTENDER_RESPONSE_TOO_LONG(400, "응답이 허용된 길이를 초과했습니다."),
    BARTENDER_INVALID_TIME(500, "시간대 설정 중 오류가 발생했습니다."),
    BARTENDER_INVALID_MARKER(500, "추천 마커 처리 중 오류가 발생했습니다."),
    BARTENDER_MAX_HISTORY_EXCEEDED(400, "최대 채팅 기록 제한을 초과했습니다."),
    BARTENDER_GREETING_ERROR(500, "인사말 생성 중 오류가 발생했습니다."),

    // Cocktail Error Codes (칵테일 도메인)
    COCKTAIL_NOT_FOUND(404, "조회된 칵테일이 없습니다."),
    INVALID_SORT_PARAMETER(400, "잘못된 정렬 파라미터입니다."),
    INVALID_BASE_SPIRIT(400, "잘못된 베이스 스피릿 종류입니다."),
    SEARCH_KEYWORD_EMPTY(400, "검색어가 비어있습니다."),

    // Cocktail Detail Error Codes (칵테일 상세 도메인)
    COCKTAIL_DETAIL_NOT_FOUND(404, "칵테일을 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없습니다."),
    UNAUTHORIZED_COMMENT_ACCESS(403, "댓글 작성자가 아닙니다."),
    LIKE_NOT_FOUND(404, "좋아요를 찾을 수 없습니다."),
    LIKE_ALREADY_EXISTS(400, "이미 좋아요를 누른 칵테일입니다."),
    COMMENT_CONTENT_EMPTY(400, "댓글 내용이 비어있습니다."),
    COMMENT_TOO_LONG(400, "댓글이 허용된 길이를 초과했습니다.");


    private final int status;
    private final String message;
}