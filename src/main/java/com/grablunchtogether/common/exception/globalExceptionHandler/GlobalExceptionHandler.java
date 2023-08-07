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
}

