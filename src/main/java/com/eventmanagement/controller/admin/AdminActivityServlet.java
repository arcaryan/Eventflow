package com.eventmanagement.controller.admin;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/activity")
public class AdminActivityServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("activity",DAOFactory.activity().findRecent(100));forward(req,res,"admin/activity.jsp");}
}
