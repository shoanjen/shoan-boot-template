package cn.shoanadmin.domain.request;

import cn.shoanadmin.domain.request.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "微信登录请求对象")
public class LoginReq extends BaseReq {
    
    @Schema(description = "微信授权码", example = "081Kq4Ga1e3EW02uT9Ga1sF-W53Kq4Gd", required = true)
    private String code;        // 微信授权码
    
    @Schema(description = "用户昵称", example = "果粒用户")
    private String nickname;    // 用户昵称（可选）
    
    @Schema(description = "用户头像URL", example = "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKxrUx8UiaBnzhiaic62R5jhFEoTgTwXBjM/132")
    private String avatarUrl;
}
