package com.cjg.home.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
public enum ResultCode {

    USER_SAVE_SUCCESS(HttpStatus.CREATED, "사용자 저장 성공"),
    USER_SEARCH_SUCCESS(HttpStatus.OK, "사용자 조회 성공"),
    USER_MODIFY_SUCCESS(HttpStatus.OK, "사용자 수정 성공"),
    USER_DELETE_SUCCESS(HttpStatus.OK, "사용자 삭제 성공"),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),

    USER_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 없습니다"),
    USER_INVALID_USERID(HttpStatus.BAD_REQUEST, "아이디가 적합하지 않습니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 적합하지 않습니다."),
    USER_INVALID_IMAGE(HttpStatus.BAD_REQUEST, "사진이 적합하지 않습니다."),
    USER_INVALID_AUTH(HttpStatus.BAD_REQUEST, "권한이 적합하지 않습니다."),

    IO_FAIL(HttpStatus.BAD_REQUEST, "이미지 처리가 실패하였습니다"),

    S3_IO_FAIL(HttpStatus.BAD_REQUEST, "이미지 처리가 실패하였습니다"),
    S3_EXT_FAIL(HttpStatus.BAD_REQUEST, "이미지 확장자가 맞지 않습니다"),
    S3_SDK_FAIL(HttpStatus.BAD_REQUEST, "이미지 업로드에 실패하였습니다"),

    POST_TEMP_SAVE_SUCCESS(HttpStatus.CREATED, "게시글 임시 저장 성공"),
    POST_TEMP_SAVE_FAIL(HttpStatus.BAD_REQUEST, "게시글 임시 저장 실패"),

    POST_SAVE_SUCCESS(HttpStatus.CREATED, "게시글 저장 성공"),
    POST_SEARCH_SUCCESS(HttpStatus.OK, "게시글 조회 성공"),
    POST_LIST_SUCCESS(HttpStatus.OK, "게시글 리스트 조회 성공"),
    POST_MODIFY_SUCCESS(HttpStatus.OK, "게시글 수정 성공"),
    POST_DELETE_SUCCESS(HttpStatus.OK, "게시글 삭제 성공"),

    POST_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 없습니다"),
    POST_INVALID_TITLE(HttpStatus.BAD_REQUEST, "제목이 적합하지 않습니다."),
    POST_INVALID_CONTENT(HttpStatus.BAD_REQUEST, "내용이 적합하지 않습니다."),
    POST_INVALID_OPEN(HttpStatus.BAD_REQUEST, "공개여부가 적합하지 않습니다."),
    POST_INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "페이지 사이즈가 적합하지 않습니다"),
    POST_INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "페이지 번호가 적합하지 않습니다"),
    POST_INVALID_AUTH(HttpStatus.BAD_REQUEST, "게시판 권한이 없습니다"),

    INVALID_PARAM(HttpStatus.BAD_REQUEST, "적합하지 않은 파라미터입니다"),

    JWT_EXPIRE(HttpStatus.BAD_REQUEST, "인증토큰 만료"),
    JWT_ERROR(HttpStatus.BAD_REQUEST, "인증토큰 에러"),

    REDIS_CONNECTION(HttpStatus.BAD_REQUEST, "레디스 연결 에러"),

    AES_FAIL(HttpStatus.BAD_REQUEST, "AES암호화 에러"),

    PAGE_INVALID_SIZE(HttpStatus.BAD_REQUEST, "페이지 사이즈가 적합하지 않습니다"),
    PAGE_INVALID_NUMBER(HttpStatus.BAD_REQUEST, "페이지 번호가 적합하지 않습니다"),

    COMMENT_SAVE_SUCCESS(HttpStatus.CREATED, "댓글 저장 성공"),
    COMMENT_MODIFY_SUCCESS(HttpStatus.OK, "댓글 수정 성공"),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "댓글 삭제 성공"),

    COMMENT_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 조회 실패"),
    COMMENT_INVALID_AUTH(HttpStatus.NOT_FOUND, "댓글 권한이 없습니다"),

    OAUTH_LOGIN_INVALID_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "미등록된 OAuth2로그인 제공자"),
    OAUTH_LOGIN_EXCEPTION(HttpStatus.BAD_REQUEST, "로그인 처리중 에러"),
    OAUTH_ACCESS_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "OAUTH2 ACCESS토큰 에러"),
    OAUTH_USER_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, "OAUTH2 사용자 정보 에러"),

    SUBSCRIBE_SAVE_SUCCESS(HttpStatus.CREATED, "구독 저장 성공"),
    SUBSCRIBE_SAVE_FAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "구독 저장 실패(중복 구독)"),
    SUBSCRIBE_DELETE_SUCCESS(HttpStatus.OK, "구독 취소 성공"),
    SUBSCRIBE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "구독 취소 실패"),

    ALARM_LIST_SUCCESS(HttpStatus.OK, "알람 리스트 성공"),
    ALARM_DELETE_SUCCESS(HttpStatus.OK, "알람 삭제 성공"),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "알람 조회 실패"),

    ALARM_COUNT_SUCCESS(HttpStatus.OK, "알람 카운트 성공"),
    ALARM_CHECK_SUCCESS(HttpStatus.OK, "알람 체크 성공");




    private final HttpStatus httpStatus;
    private final String message;

    public String getCode() {
        return String.valueOf(httpStatus.value());
    }
}
