package com.eventmanagement.controller.auth;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.exception.BusinessRuleException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.User;
import com.eventmanagement.service.AuthService;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private final AuthService auth=new AuthService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException, jakarta.servlet.ServletException{if(currentUser(req)!=null){redirect(req,res,"/home");return;}forward(req,res,"auth/login.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException, jakarta.servlet.ServletException{try{User user=auth.authenticate(req.getParameter("email"),req.getParameter("password"));req.getSession(true);req.changeSessionId();req.getSession().setAttribute(ServletUtil.CURRENT_USER,user);String target=switch(user.getRole()){case ADMIN->"/admin/dashboard";case ORGANIZER->"/organizer/dashboard";case ATTENDEE->"/attendee/dashboard";};redirect(req,res,target);}catch(ValidationException|BusinessRuleException e){req.setAttribute("error",e.getMessage());req.setAttribute("email",req.getParameter("email"));forward(req,res,"auth/login.jsp");}}
}
