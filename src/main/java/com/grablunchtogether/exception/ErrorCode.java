package com.grablunchtogether.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // ex) NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원 정보를 찾을 수 없습니다.")
    // 공통 Exception
    USER_SIGN_UP_FAIL(HttpStatus.BAD_REQUEST, "회원가입에 실패했습니다."),
    USER_SIGN_IN_FAIL(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지않습니다."),
    USER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보를 찾을 수 없습니다."),
    UNDEFINED_EXCEPTION(HttpStatus.BAD_REQUEST, "정의되지 않은 예외가 발생했습니다. 메세지를 참고해주세요."),
    TOKEN_IS_INVALID(HttpStatus.BAD_REQUEST, "토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요."),
    PLAN_TIME_NOT_MATCH(HttpStatus.BAD_REQUEST, "약속시간 1시간 이전에만 가능합니다."),
    USER_REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    NOT_PERMITTED(HttpStatus.BAD_REQUEST, "해당 데이터에 대한 접근 권한이 없습니다."),
    CRAWLING_IS_IN_PROGRESS(HttpStatus.BAD_REQUEST, "맛집정보를 업데이트하는 중입니다. 잠시후 다시 시도해주세요."),
    NOT_AVAILABLE_TO_CANCEL(HttpStatus.BAD_REQUEST, "이미 수락 또는 거절된 점심약속은 삭제할 수 없습니다. 점심약속 취소를 진행 해주세요."),
    CAN_NOT_EDIT_PLAN(HttpStatus.BAD_REQUEST, "요청중인 상태의 점심약속만 수정할 수 있습니다."),
    CAN_NOT_CANCEL_PLAN(HttpStatus.BAD_REQUEST, "이미 취소 되었거나 취소할 수 없는상태의 점심약속입니다."),
    PLAN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "상대방에게 신청한 '요청중' 상태의 점심약속이 존재합니다.\n기존점심약속이 수락/거절되었거나 완료된 경우 다시 신청 할 수 있습니다."),
    MUST_EAT_PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 맛집정보입니다."),
    BOOKMARK_SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 즐겨찾기 맛집정보입니다."),
    FAVORITE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 즐겨찾기 유저정보입니다."),
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 약속정보입니다."),
    PLAN_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 약속 히스토리 입니다."),
    USER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰 입니다."),
    CAN_NOT_COMPLETE_PLAN(HttpStatus.BAD_REQUEST,"수락되지 않은 약속입니다." );

    private final HttpStatus status;
    private final String message;
}