package com.chatbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Web configuration:
 * - Serves static files (index.html, CSS, JS) from /static folder
 * - Redirects root "/" to index.html
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect root to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
