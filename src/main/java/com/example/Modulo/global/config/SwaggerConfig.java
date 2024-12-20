package com.example.Modulo.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.username}")
    private String swaggerUsername;

    @Value("${swagger.password}")
    private String swaggerPassword;

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";

        SecurityScheme jwtSecurityScheme = new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityScheme basicSecurityScheme = new SecurityScheme()
                .name("basicAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic");

        Server server = new Server();
        server.setUrl("https://api.modulo.p-e.kr");

        SecurityRequirement globalSecurityRequirement = new SecurityRequirement()
                .addList(jwt);

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(jwt, jwtSecurityScheme)
                        .addSecuritySchemes("basicAuth", basicSecurityScheme))
                .info(new Info()
                        .title("Modulo API Documentation")
                        .description("Modulo 프로젝트의 API 명세서입니다. 인증이 필요한 API는 JWT 토큰이 필요합니다.")
                        .version("v1.0.0"))
                .addSecurityItem(globalSecurityRequirement)
                .addServersItem(server);
    }

    @Bean
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatchers((matchers) -> matchers
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .csrf(csrf -> csrf.disable())
                .build();
    }

    @Bean
    public InMemoryUserDetailsManager swaggerUserDetailsManager() {
        UserDetails user = User.withUsername(swaggerUsername)
                .password(passwordEncoder().encode(swaggerPassword))
                .roles("SWAGGER_ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}