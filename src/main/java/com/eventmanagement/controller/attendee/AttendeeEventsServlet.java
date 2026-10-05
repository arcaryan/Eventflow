package com.eventmanagement.controller.attendee;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/attendee/events")
public class AttendeeEventsServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("events",DAOFactory.events().findApproved(req.getParameter("search"),req.getParameter("category")));forward(req,res,"attendee/events.jsp");}
}
