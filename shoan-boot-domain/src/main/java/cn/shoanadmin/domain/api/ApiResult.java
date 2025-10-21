package cn.shoanadmin.domain.api;


import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResult<T>{
    private String code;
    private String message;
    private T data;

    /**
     * @description 响应成功返回数据
     * @param data
     * @return
     */
    public static <T> ApiResult<T> success(T data) {
        return ApiResult
                .<T>builder()
                .code("200")
                .message("success")
                .data(data)
                .build();
    }

    /**
     * @description 响应成功,不返回数据
     * @return
     */
    public static <T> ApiResult<T> success() {
        return ApiResult
                .<T>builder()
                .code("200")
                .message("success")
                .data(null)
                .build();
    }

    /**
     * 响应失败，不返回data
     * @return
     */
    public static <T> ApiResult<T> error() {
        return ApiResult
                .<T>builder()
                .code("500")
                .message("server error")
                .data(null)
                .build();
    }

    /**
     * 响应失败，不返回data
     * @param businessCodeEnum
     * @return
     */
    public static <T> ApiResult<T> error(BusinessCodeEnum businessCodeEnum) {
        return ApiResult
                .<T>builder()
                .code(businessCodeEnum.getCode())
                .message(businessCodeEnum.getMessage())
                .build();
    }

    /**
     * 响应失败，不返回data
     * @param businessException
     * @return
     */
    public static <T> ApiResult<T> error(BusinessException businessException) {
        return ApiResult
                .<T>builder()
                .code(businessException.getCode())
                .message(businessException.getMessage())
                .build();
    }
}
