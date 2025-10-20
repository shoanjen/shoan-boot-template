package cn.shoanadmin.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "用户资料响应对象")
public class UserProfileRes {
    
    @Schema(description = "用户ID", example = "user_123456789")
    private String userId;
    
    @Schema(description = "用户昵称", example = "果粒用户")
    private String nickname;
    
    @Schema(description = "用户头像URL", example = "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKxrUx8UiaBnzhiaic62R5jhFEoTgTwXBjM/132")
    private String avatarUrl;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;

    /**
     * 国家
     */
    @Schema(description = "国家", example = "中国")
    private String country;

    /**
     * 省份
     */
    @Schema(description = "省份", example = "广东省")
    private String province;

    /**
     * 城市
     */
    @Schema(description = "城市", example = "深圳市")
    private String city;
}
