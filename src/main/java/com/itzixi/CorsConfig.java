package com.itzixi;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置。
 * 允许前端页面在不同端口或域名下访问当前后端接口，
 * 便于本地调试静态页面、SSE 推送与 AI 接口调用。
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置全局 CORS 规则。
     *
     * @param registry Spring MVC 提供的跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
