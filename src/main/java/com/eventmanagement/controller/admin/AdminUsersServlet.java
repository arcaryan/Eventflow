package com.eventmanagement.controller.admin;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.exception.BusinessRuleException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.PasswordUtil;
import com.eventmanagement.util.ServletUtil;
import com.eventmanagement.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/users")
public class AdminUsersServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("users",DAOFactory.users().findAll(req.getParameter("search"),req.getParameter("role"),req.getParameter("status")));req.setAttribute("search",req.getParameter("search"));req.setAttribute("role",req.getParameter("role"));req.setAttribute("status",req.getParameter("status"));forward(req,res,"admin/users.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{String action=req.getParameter("action");var dao=DAOFactory.users();if("create".equals(action)){User u=new User(null,ValidationUtil.required(req.getParameter("name"),"Name"),ValidationUtil.email(req.getParameter("email")),PasswordUtil.hash(ValidationUtil.password(req.getParameter("password"))),UserRole.valueOf(req.getParameter("role")),AccountStatus.ACTIVE);u.setPhone(req.getParameter("phone"));dao.create(u);ServletUtil.flash(req,"success","User created.");}else{long id=Long.parseLong(req.getParameter("id"));User u=dao.findById(id);if(u==null)throw new BusinessRuleException("User not found.");if("deactivate".equals(action)){u.setStatus(AccountStatus.INACTIVE);dao.updateAdmin(u);ServletUtil.flash(req,"success","User deactivated.");}else if("update".equals(action)){u.setName(ValidationUtil.required(req.getParameter("name"),"Name"));u.setEmail(ValidationUtil.email(req.getParameter("email")));u.setRole(UserRole.valueOf(req.getParameter("role")));u.setStatus(AccountStatus.valueOf(req.getParameter("status")));dao.updateAdmin(u);ServletUtil.flash(req,"success","User updated.");}}redirect(req,res,"/admin/users");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());redirect(req,res,"/admin/users");}}
}
