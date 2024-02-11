package WilesWebBackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import java.util.Arrays;

@ComponentScan(basePackages = "WilesWebBackend")
@Configuration
@EnableWebSecurity()
public class SpringSecurityConfig {

    private final CORSFilter myCorsFilter;

    private final Environment environment;

    public SpringSecurityConfig(CORSFilter myCorsFilter, Environment environment) {
        this.myCorsFilter = myCorsFilter;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();

        http.addFilterBefore(myCorsFilter, ChannelProcessingFilter.class)
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll());
        if(Arrays.asList(this.environment.getActiveProfiles()).contains("prod"))
        {
            //noinspection FunctionalExpressionCanBeFolded
            http.csrf((csrf) -> csrf
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(delegate::handle));
        }
        else{
            http.csrf(AbstractHttpConfigurer::disable);
        }
        return http.build();
    }


}