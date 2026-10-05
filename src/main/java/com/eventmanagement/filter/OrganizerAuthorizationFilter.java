package com.eventmanagement.filter;

import com.eventmanagement.model.User;
import com.eventmanagement.model.UserRole;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/organizer/*")
public class OrganizerAuthorizationFilter implements Filter {
    @Override public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{User u=ServletUtil.currentUser((HttpServletRequest)request);if(u==null||u.getRole()!=UserRole.ORGANIZER){((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);return;}chain.doFilter(request,response);}
}
