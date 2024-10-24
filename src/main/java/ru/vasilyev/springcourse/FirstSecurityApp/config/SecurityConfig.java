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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.vasilyev.springcourse.FirstSecurityApp.services.PersonDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;

    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/login", "/error", "/auth/registration").permitAll()  // Разрешить доступ к страницам логина и ошибки
                        .anyRequest().authenticated()                           // Все остальные запросы требуют аутентификации
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
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))  // Разрешить GET-запросы на /logout
                );


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
        return NoOpPasswordEncoder.getInstance();
    }
}
