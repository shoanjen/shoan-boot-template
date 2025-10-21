package cn.shoanadmin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;

/**
 * 微信用户实体类
 * 对应数据库表：wechat_user
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wechat_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（格式：FP+时间戳前4位+随机数4位）
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 微信openid
     */
    @TableField("openid")
    private String openid;

    /**
     * 微信unionid
     */
    @TableField("unionid")
    private String unionid;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 语言
     */
    @TableField("language")
    private String language;

    /**
     * 会话密钥
     */
    @TableField("session_key")
    private String sessionKey;

    /**
     * 最后登录时间（时间戳）
     */
    @TableField("last_login_time")
    private Long lastLoginTime;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间（时间戳）
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Long createdTime;

    /**
     * 更新时间（时间戳）
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private Long updatedTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}