package cn.shoanadmin.domain.request.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "基础请求对象")
public class BaseReq implements Serializable {
    /**
     * 请求id
     */
    @Schema(description = "请求ID", example = "req_123456789")
    private String requestId;
    
    /**
     * 签名值
     */
    @Schema(description = "签名值", example = "abc123def456")
    private String sign;
    
    /**
     * 请求时间戳
     */
    @Schema(description = "请求时间戳", example = "1640995200000")
    private String timestamp;
}

