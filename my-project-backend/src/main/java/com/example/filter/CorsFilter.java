package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {
    //RMBR 问题: 为什么这里的自定过滤器不用使用addFilterBefore方法添加到SecurityFilterChain方法中
    //答: 因为我们将自定过滤器标记为了Component,并且继承了HttpFilter,所以springboot将自动隐式添加到过滤器链中
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request,response);
        chain.doFilter(request, response);
    }

    private void addCorsHeader(HttpServletRequest request,
                               HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));//接受所有请求
        response.addHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
        response.addHeader("Access-Control-Allow-Headers","Authorization,Content-Type");
    }
}
