package cn.shoanadmin.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "cn.shoanadmin")
@MapperScan("cn.shoanadmin.infrastructure.mapper")
public class ShoanAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoanAdminApplication.class, args);
        System.out.println("📚 API文档地址: http://localhost:8080/doc.html");
    }
}