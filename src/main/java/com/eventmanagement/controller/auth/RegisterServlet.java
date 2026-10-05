package com.eventmanagement.controller.auth;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.exception.BusinessRuleException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.UserRole;
import com.eventmanagement.service.AuthService;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final AuthService auth=new AuthService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException, jakarta.servlet.ServletException{if(currentUser(req)!=null){redirect(req,res,"/home");return;}forward(req,res,"auth/register.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException, jakarta.servlet.ServletException{try{UserRole role=UserRole.valueOf(req.getParameter("role"));auth.register(req.getParameter("name"),req.getParameter("email"),req.getParameter("password"),req.getParameter("phone"),role);ServletUtil.flash(req,"success","Account created. You can sign in now.");redirect(req,res,"/login");}catch(IllegalArgumentException|ValidationException|BusinessRuleException e){req.setAttribute("error",e.getMessage());forward(req,res,"auth/register.jsp");}}
}
