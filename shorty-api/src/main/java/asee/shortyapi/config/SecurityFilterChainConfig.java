package asee.shortyapi.config;

import asee.shortyapplication.config.CustomJwtAuthConverter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityFilterChainConfig {

    private final CustomJwtAuthConverter customJwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(x -> x
                        .requestMatchers("/swagger-ui/**").hasRole("client_developer")
                        .requestMatchers("/v3/api-docs/**").hasRole("client_developer")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(x ->
                        x.jwt(y ->
                            y.jwtAuthenticationConverter(customJwtAuthConverter)))
                .httpBasic(withDefaults());

        return http.build();
    }
}
