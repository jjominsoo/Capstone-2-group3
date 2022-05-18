package youandme.youandme;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:images/");
        registry.addResourceHandler("/graduation_certification/**").addResourceLocations("file:graduation_certification/");
        registry.addResourceHandler("/company_certification/**").addResourceLocations("file:company_certification/");
    }
}