package com.acfun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(WebSecurity security) {
    security.ignoring().antMatchers("/css/**", "/fonts/**", "/js/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/ws/admin", "/v1/api/admin/*").authenticated()
        .anyRequest().permitAll()
        .and()
        .formLogin().usernameParameter("name").passwordParameter("password").permitAll()
        .and()
        .logout().permitAll()
        .and().exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
        .and().httpBasic();
  }

}
