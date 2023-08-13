package WilesWebBackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@ComponentScan(basePackages = "WilesWebBackend")
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    private CORSFilter myCorsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(myCorsFilter, ChannelProcessingFilter.class)
                .authorizeHttpRequests()
                .anyRequest().permitAll()
                .and().csrf().disable();
        return http.build();
    }


}