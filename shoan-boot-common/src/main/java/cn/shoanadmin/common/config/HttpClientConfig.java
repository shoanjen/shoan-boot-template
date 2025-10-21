package cn.shoanadmin.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP客户端配置类
 * 配置RestTemplate等HTTP客户端相关Bean
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Configuration
public class HttpClientConfig {

    /**
     * 配置RestTemplate Bean
     * 用于HTTP请求调用，如微信API调用
     * 
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 连接超时5秒
        factory.setReadTimeout(10000);   // 读取超时10秒
        
        return new RestTemplate(factory);
    }
}