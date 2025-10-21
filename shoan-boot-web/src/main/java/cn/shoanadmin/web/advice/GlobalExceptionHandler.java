package cn.shoanadmin.web.advice;

import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import cn.shoanadmin.domain.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler<T> {

    @ExceptionHandler(BusinessException.class)
    public ApiResult<T> handleBusinessException(BusinessException e, HttpServletRequest request) {
        return ApiResult.error(e);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<T> handleGeneralException(Exception e, HttpServletRequest request) {
        return ApiResult.error(BusinessCodeEnum.SYSTEM_ERROR);
    }
}