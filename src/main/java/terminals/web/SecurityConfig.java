package terminals.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private AuthenticationProvider authProvider;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/").anonymous()
                .antMatchers("/css/**", "/png/**").permitAll()
                .antMatchers("/**").hasAnyAuthority("root", "administrator")

                .and()
                .formLogin()

                .loginPage("/login")
                .loginProcessingUrl("/login/process")
                .usernameParameter("login")
                .defaultSuccessUrl("/main")//авторизованный пользователь редиректится сюда
                .failureUrl("/login")
                .and().logout().logoutSuccessUrl("/login")
        ;
    }


    @Autowired
    public void setAuthProvider(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }
}

