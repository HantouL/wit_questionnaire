package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dao.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils jwtUtils;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    private AccountService accountService;

    //Security过滤器链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf->conf
                        .requestMatchers("/api/auth/**","/error").permitAll() //设置验证模块的请求通过
                        .requestMatchers("/api/admin/**").hasAnyRole("admin","superAdmin") //RMBR 管理員特权接口
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/file/getFile/image/**").permitAll()//给请求头像资源的请求放行
                        .anyRequest().hasAnyRole("admin","user","superAdmin") //其他任何请求不得通过
                )
                .formLogin(conf->conf //表单登录
                        .loginProcessingUrl("/api/auth/login") //配置登录的访问
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                        )
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf->conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)//有令牌,权限不足
                )
                .csrf(AbstractHttpConfigurer::disable)// 配置csrf(跨域)
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))//设置Session为无状态
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)//RMBR 需要在用户密码过滤器之前,加入我们自定义的过滤器,这样可以减轻用户密码过滤器的负担
                .build();
    }



    //登陆成功的Handler
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        User user = (User) authentication.getPrincipal();
        Account account = accountService.findAccountByNameOrEmail(user.getUsername());
        String token = jwtUtils.createJwt(user, account.getId(),account.getUsername(),account.getRole());
        //将dto中的部分非敏感信息传到vo中再返回
        AuthorizeVO vo = new AuthorizeVO();
        BeanUtils.copyProperties(account,vo);//使用Spring提供的BeanUtils工具
        vo.setExpireDate(jwtUtils.expireTime());
        vo.setToken(token);
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    //退出登录成功的Handler
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");

        if(jwtUtils.invalidateJwt(authorization)){
            writer.write(RestBean.success().asJsonString());
        }else{
            writer.write(RestBean.failure(400,"Logout Failed").asJsonString());
        }

    }

    //权限不够的handler
    private void onAccessDeny(HttpServletRequest request,
                              HttpServletResponse response,
                              AccessDeniedException exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    //jwt未验证下的handler
    //RMBR 这里可以与未登录的handle整合,毕竟只有在登录后才会发jwtToken
    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    //登录失败时的Handler
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

}

