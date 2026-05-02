package com.diegogiron.kinalapp.config;

import com.diegogiron.kinalapp.security.CustomAuthenticationSuccessHandler;
import com.diegogiron.kinalapp.security.UsuarioSessionFilter;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final IUsuarioService usuarioService;

    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
                          IUsuarioService usuarioService) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.usuarioService = usuarioService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // añde el filtro antes del UsernamePasswordAuthenticationFilter
        http.addFilterBefore(new UsuarioSessionFilter(usuarioService),
                UsernamePasswordAuthenticationFilter.class);

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/auth/**", "/error/**").permitAll()
                        .requestMatchers("/web/dashboard/**", "/web/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/web/productos/**", "/web/clientes/**", "/web/ventas/**", "/web/detalle-ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers("/web/programador").authenticated()
                        .requestMatchers("/tienda/**", "/carrito/**").hasAnyRole("ADMIN", "VENDEDOR", "USER")
                        .requestMatchers("/clientes/**", "/productos/**", "/usuarios/**", "/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403")
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}