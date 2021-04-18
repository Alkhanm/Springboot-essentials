package com.github.alkhanm.config;

import com.github.alkhanm.service.MyUserDetailsService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService myUserDetailsService;

    public SecurityConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    // Configura quais requisições serão protegidas
    @Override protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            /* Define que as requisições que necessitam de CSRF terão que passar um token
             * Por segurança, esse Token será apenas transmitido por HTTP
             * Lembre-se de ativar isso caso esteja colocando o programa em produção:
             * .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse().and() */
              .authorizeRequests()
                //.antMatchers("/animes/admin/**").hasRole("ADMIN")
                //.antMatchers("/animes/**").hasAnyRole("ADMIN", "USER")
                .anyRequest()
                .permitAll()
                //.authenticated()
              .and()
                .httpBasic();
    }

    @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encoded = passwordEncoder.encode("test");
        System.out.printf("Password encoded: %s\n", encoded );

        auth
                //Classe responsável por buscar usuários autenticáveis
                .userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder);

        // O Spring consegue realizar a autenticação com múltiplos provedores ao mesmo tempo
        // Assim, ele aceita tanto autenticação através do banco de dados (acima) quanto em memória (abaixo)
        auth.inMemoryAuthentication()
                .withUser("alkham2")
                .password(encoded)
                .roles("USER")
                .and()
                .withUser("feosmur2")
                .password(encoded)
                .roles("USER", "ADMIN");
    }
}
