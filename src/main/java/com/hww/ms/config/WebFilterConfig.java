package com.hww.ms.config;

import com.hww.ms.interceptor.IPInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebFilterConfig implements WebMvcConfigurer {

    /**
     * 这里需要先将限流拦截器入住，不然无法获取到拦截器中的redistemplate
     * @return
     */
    @Bean
    public IPInterceptor getIPInterceptor() {
        return new IPInterceptor();
    }

    /**
     * 多个拦截器组成一个拦截器链
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getIPInterceptor()).addPathPatterns("/**");
    }


}
