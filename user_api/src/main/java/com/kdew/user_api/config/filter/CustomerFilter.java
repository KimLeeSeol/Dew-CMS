package com.kdew.user_api.config.filter;

import com.kdew.dewdomain.common.UserVo;
import com.kdew.dewdomain.config.JwtAuthenticationProvider;
import com.kdew.user_api.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/customer/*")
@RequiredArgsConstructor
public class CustomerFilter implements Filter {
    // cutomer로 들어가는 모든 패턴에 대해서 필터를 적용

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;

    // 필터를 통해서
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("X-AUTH-TOKEN");
        if (!provider.validateToken(token)) {
            throw new ServletException("Invalid Access");
        }
        UserVo vo = provider.getUserVo(token);
        customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                () -> new ServletException("Invalid access")
        );
        chain.doFilter(request,response);
    }
}
