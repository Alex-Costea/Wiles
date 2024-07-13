package WilesWebBackend;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        Objects.requireNonNull(registry);

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(35)));
    }
}