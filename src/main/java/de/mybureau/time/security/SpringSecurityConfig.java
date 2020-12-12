package de.mybureau.time.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.List;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final String userLogin;
    private final String userPassword;

    @Autowired
    public SpringSecurityConfig(JwtTokenProvider jwtTokenProvider,
                                @Value("${user.login}") String userLogin,
                                @Value("${user.password}") String userPassword) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                    .antMatchers("/v1/auth/login").permitAll()
                    .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                    .addFilterBefore(tokenAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        final var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        authProvider.setForcePrincipalAsString(true);

        auth.authenticationProvider(authProvider)
                .userDetailsService(userDetailsService());
    }

    /**
     * Exposing this bean for the AuthResource
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceAdapter(userLogin, userPassword);
    }

    @Bean
    public TokenAuthorizationFilter tokenAuthorizationFilter() throws Exception {
        return new TokenAuthorizationFilter(userDetailsServiceBean(), securityService());
    }

    @Bean
    public SecurityService securityService() throws Exception {
        return new SecurityService(authenticationManager(), jwtTokenProvider);
    }
}

class UserDetailServiceAdapter implements UserDetailsService {

    private final String userLogin;
    private final String userPassword;

    public UserDetailServiceAdapter(String userLogin, String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userLogin.equals(username)) {
            return new User(userLogin, userPassword, authorities("user"));
        } else {
            throw new UsernameNotFoundException(String.format("No user with '%s' username found!", username));
        }
    }

    private static Collection<? extends GrantedAuthority> authorities(String role) {
        return List.of(() -> role);
    }

}
