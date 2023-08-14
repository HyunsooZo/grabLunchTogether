package com.grablunchtogether.common.exception.globalExceptionHandler;

import com.grablunchtogether.common.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    //Exception 핸들러(토큰검증 실패)
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> InvalidTokenExceptionHandler(InvalidTokenException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(찾는내용 없음)
    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<String> ContentNotFoundExceptionHandler(ContentNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    //Exception 핸들러(회원가입 실패)
    @ExceptionHandler(UserSignUpException.class)
    public ResponseEntity<String> UserUserSignUpExceptionHandler(UserSignUpException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(로그인검증 실패)
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<String> InvalidLoginExceptionHandler(InvalidLoginException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(고객정보불러오기 실패)
    @ExceptionHandler(UserInfoNotFoundException.class)
    public ResponseEntity<String> UserInfoNotFoundExceptionHandler(UserInfoNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    //Exception 핸들러(동일인물에게 '요청중' 상태의 약속이 있는데 다시 약속 신청)
    @ExceptionHandler(ExistingPlanException.class)
    public ResponseEntity<String> ExistingPlanExceptionHandler(ExistingPlanException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(이미 수락/거절/만료 된 점심약속의 상태를 변경하려고 할 경우)
    @ExceptionHandler(AuthorityException.class)
    public ResponseEntity<String> AuthorityExceptionHandler(AuthorityException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(이미 수락/거절/만료 된 점심약속의 상태를 변경하려고 할 경우)
    @ExceptionHandler(PlanTimeNotMatchedException.class)
    public ResponseEntity<String> PlanTimeNotMatchedExceptionHandler(PlanTimeNotMatchedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(이미 수락/거절/만료 된 점심약속의 상태를 변경하려고 할 경우)
    @ExceptionHandler(CrawlingIsInProgressException.class)
    public ResponseEntity<String> CrawlingIsInProgressException(CrawlingIsInProgressException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(이미 해당 점심약속에 대해 작성한 리뷰가 존재할때)
    @ExceptionHandler(UserReviewAlreadyExistsException.class)
    public ResponseEntity<String> UserReviewAlreadyExistsExceptionHandler(UserReviewAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Exception 핸들러(그 외 모든 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> ExceptionHandler(Exception exception) {
        return new ResponseEntity<>("알수없는 에러 : \n"+exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

