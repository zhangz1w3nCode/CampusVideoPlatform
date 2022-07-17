package com.zzw;

import com.zzw.service.webSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling

public class Blibili_App {
    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(Blibili_App.class, args);
        webSocketService.setApplicationContext(app);//解决多例模式的bean注入问题
    }
}
