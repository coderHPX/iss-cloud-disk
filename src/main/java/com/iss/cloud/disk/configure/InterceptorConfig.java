package com.iss.cloud.disk.configure;


import com.iss.cloud.disk.handler.LoginHandle;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandle())
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/","/register","/user/register");

    }
}
