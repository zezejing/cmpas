package com.hnisc.cmpas;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@MapperScan("com.hnisc.cmpas.mapper")
public class CmpasApplication extends WebMvcConfigurerAdapter {
    //是否允许跨域请求
    private final boolean allowCrossDomanRequest=true;

    public static void main(String[] args) {
        SpringApplication.run(CmpasApplication.class, args);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (allowCrossDomanRequest)
            registry.addMapping("/**")
                    .allowCredentials(true)
                    .allowedHeaders("*")
                    .allowedOrigins("*")
                    .allowedMethods("*");
    }
    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize("150MB");
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("150MB");
        return factory.createMultipartConfig();
    }
}
