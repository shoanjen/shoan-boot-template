package cn.shoanadmin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置类
 * 用于管理微信小程序的相关配置信息
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappConfig {

    /**
     * 小程序AppId
     */
    private String appId;

    /**
     * 小程序AppSecret
     */
    private String appSecret;

    /**
     * 微信API基础URL
     */
    private String apiBaseUrl = "https://api.weixin.qq.com";

    /**
     * 获取用户openid和session_key的接口路径
     */
    private String tokenUrl = "/sns/jscode2session";

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 10000;

    /**
     * 获取完整的jscode2session接口URL
     * 
     * @return 完整的接口URL
     */
    public String getJscode2sessionUrl() {
        return apiBaseUrl + tokenUrl;
    }
}