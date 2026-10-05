package com.eventmanagement.filter;

import com.eventmanagement.model.User;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    @Override public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
        HttpServletRequest req=(HttpServletRequest)request;HttpServletResponse res=(HttpServletResponse)response;String uri=req.getRequestURI().substring(req.getContextPath().length());
        boolean publicPath=uri.equals("")||uri.equals("/")||uri.equals("/home")||uri.startsWith("/login")||uri.startsWith("/register")||uri.startsWith("/events")||uri.startsWith("/assets/")||uri.startsWith("/index.jsp");
        User user=ServletUtil.currentUser(req);
        if(!publicPath&&user==null){res.sendRedirect(req.getContextPath()+"/login");return;}
        chain.doFilter(request,response);
    }
}
