package com.grablunchtogether.common.results.responseResult;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import org.springframework.http.ResponseEntity;

public class ResponseResult {
    public static ResponseEntity<?> result(ServiceResult result){
        if(!result.isResult()){
            return ResponseEntity.badRequest().body(
                    ResponseMessage.fail(result.getMessage()));
        }
        Object object = result.getObject();
        String message = result.getMessage();
        return ResponseEntity.ok().body(ResponseMessage.success(message,object));
    }
}
