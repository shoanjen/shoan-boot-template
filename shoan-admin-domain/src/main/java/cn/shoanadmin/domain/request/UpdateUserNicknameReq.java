package cn.shoanadmin.domain.request;

import cn.shoanadmin.domain.request.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "更新用户昵称请求对象")
public class UpdateUserNicknameReq extends BaseReq {
    
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度必须在1-20个字符之间")
    @Schema(description = "用户昵称", example = "新昵称", required = true)
    private String nickname;
}