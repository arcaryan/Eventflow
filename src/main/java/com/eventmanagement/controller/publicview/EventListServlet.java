package com.eventmanagement.controller.publicview;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/events")
public class EventListServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException, jakarta.servlet.ServletException{req.setAttribute("events",DAOFactory.events().findApproved(req.getParameter("search"),req.getParameter("category")));req.setAttribute("search",req.getParameter("search"));req.setAttribute("category",req.getParameter("category"));forward(req,res,"public/events.jsp");}
}
