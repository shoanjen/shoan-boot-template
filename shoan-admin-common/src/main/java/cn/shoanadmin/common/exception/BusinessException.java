package cn.shoanadmin.common.exception;

import cn.shoanadmin.common.enums.BusinessCodeEnum;
import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 
 * @author FruitPieces Team
 * @version 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private String code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 详细错误信息
     */
    private Object data;
    
    public BusinessException() {
        super();
    }
    
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(String code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(String code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BusinessException(BusinessCodeEnum businessCodeEnum, Object data) {
        this.code = businessCodeEnum.getCode();
        this.message = businessCodeEnum.getMessage();
        this.data = data;
    }

    public BusinessException(BusinessCodeEnum businessCodeEnum) {
        this.code = businessCodeEnum.getCode();
        this.message = businessCodeEnum.getMessage();
    }
}
