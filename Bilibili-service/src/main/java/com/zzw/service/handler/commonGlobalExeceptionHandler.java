package com.zzw.service.handler;


import com.zzw.domain.jsonResponse;
import com.zzw.exception.conditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class commonGlobalExeceptionHandler {

    @ExceptionHandler
    @ResponseBody
    //全局异常处理接口
    public jsonResponse<String> commonExceptionHandler(HttpServletRequest request,Exception e){
        String errorMsg = e.getMessage();
        if(e instanceof conditionException){
            String errorCode = ((conditionException) e).getCode();
             return  new jsonResponse<>(errorCode,errorMsg);
        }else{
            return new jsonResponse<>("500",errorMsg);
        }
    }
}
