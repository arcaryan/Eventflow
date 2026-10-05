package com.eventmanagement.controller.admin;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/settings")
public class AdminSettingsServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("settings",DAOFactory.settings().findAll());forward(req,res,"admin/settings.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{var dao=DAOFactory.settings();for(String key:new String[]{"platform_name","support_email","default_currency","registration_enabled","max_tickets_per_attendee","event_approval_required"})dao.update(key,req.getParameter(key));ServletUtil.flash(req,"success","Settings saved.");redirect(req,res,"/admin/settings");}
}
