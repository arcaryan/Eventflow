package com.eventmanagement.controller.attendee;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.model.User;
import com.eventmanagement.util.ServletUtil;
import com.eventmanagement.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/attendee/profile")
public class AttendeeProfileServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{forward(req,res,"attendee/profile.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{try{User u=currentUser(req);u.setName(ValidationUtil.required(req.getParameter("name"),"Name"));u.setPhone(req.getParameter("phone"));u.setAddress(req.getParameter("address"));u.setNotificationPreference(req.getParameter("notificationPreference")!=null);DAOFactory.users().updateProfile(u);req.getSession().setAttribute(ServletUtil.CURRENT_USER,u);ServletUtil.flash(req,"success","Profile updated.");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());}redirect(req,res,"/attendee/profile");}
}
