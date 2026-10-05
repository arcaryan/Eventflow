package com.eventmanagement.controller.admin;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("stats",DAOFactory.events().systemStats());req.setAttribute("pendingEvents",DAOFactory.events().findPending());req.setAttribute("activity",DAOFactory.activity().findRecent(8));req.setAttribute("recentUsers",DAOFactory.users().findAll("","","").stream().limit(6).toList());forward(req,res,"admin/dashboard.jsp");}
}
