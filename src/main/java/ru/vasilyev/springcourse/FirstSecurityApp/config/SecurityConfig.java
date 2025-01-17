//
package ru.vasilyev.springcourse.FirstSecurityApp.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import ru.vasilyev.springcourse.FirstSecurityApp.services.PersonDetailsService;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final PersonDetailsService personDetailsService;
//
//    @Autowired
//    public SecurityConfig(PersonDetailsService personDetailsService) {
//        this.personDetailsService = personDetailsService;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/auth/login", "/error").permitAll()  // Разрешить доступ к страницам логина и ошибки
//                        .anyRequest().authenticated()                          // Все остальные запросы требуют аутентификации
//                )
//                .formLogin(form -> form
//                        .loginPage("/auth/login")              // Указываем страницу логина
//                        .loginProcessingUrl("/process_login")  // URL для отправки формы логина
//                        .defaultSuccessUrl("/hello", true)     // Перенаправление после успешного входа
//                        .failureUrl("/auth/login?error")       // Перенаправление при неудачном входе
//                        .permitAll()                           // Разрешаем доступ ко всем пользователям
//                );
//
//        return http.build();
//    }
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .authorizeHttpRequests((requests) -> requests
////                        .requestMatchers("/", "/home", "/error", "/css/**", "/js/**").permitAll()
////                        .anyRequest().authenticated()
////                )
////                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll
////                )
////                .logout(LogoutConfigurer::permitAll);
////
////        return http.build();
////    }
//
//    @Bean
//    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authBuilder.userDetailsService(personDetailsService);
//        return authBuilder.build();
//    }
//
//    @Bean
//    public PasswordEncoder getPasswordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
//}



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.vasilyev.springcourse.FirstSecurityApp.services.PersonDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;
    private final JWTFilter jwtFilter;

    public SecurityConfig(PersonDetailsService personDetailsService,
                          JWTFilter jwtFilter) {
        this.personDetailsService = personDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/auth/login", "/error", "/auth/registration").permitAll()  // Разрешить доступ к страницам логина и ошибки
                        .anyRequest().hasAnyRole("USER", "ADMIN")                           // Все остальные запросы требуют аутентификации
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")                // Указываем страницу логина
                        .loginProcessingUrl("/process_login")    // URL для обработки логина
                        .defaultSuccessUrl("/hello", true)       // Перенаправление после успешного входа
                        .failureUrl("/auth/login?error")         // Перенаправление при неудачном входе
                        .permitAll()// Разрешаем доступ ко всем пользователям
                ).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login")
                        .permitAll()                      // Разрешить всем доступ к /logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))  // Разрешить GET-запросы на /logout
                ).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(personDetailsService).passwordEncoder(getPasswordEncoder());
        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
