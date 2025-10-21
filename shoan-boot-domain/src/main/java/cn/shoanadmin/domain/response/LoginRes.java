package cn.shoanadmin.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "登录响应对象")
public class LoginRes {
    
    @Schema(description = "用户ID", example = "user_123456789")
    private String userId;
    
    @Schema(description = "用户昵称", example = "果粒用户")
    private String nickname;
    
    @Schema(description = "用户头像URL", example = "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKxrUx8UiaBnzhiaic62R5jhFEoTgTwXBjM/132")
    private String avatarUrl;
    
    @Schema(description = "最后登录时间戳", example = "1640995200000")
    private Long lastLoginTime;

    @Schema(description = "访问token", example = "jfakadjfkgffnnn")
    private String accessToken;
}
