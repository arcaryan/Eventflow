package com.eventmanagement.controller.attendee;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/attendee/dashboard")
public class AttendeeDashboardServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{long id=currentUser(req).getId();req.setAttribute("events",DAOFactory.events().findUpcomingForAttendee(id));req.setAttribute("orders",DAOFactory.orders().findByAttendee(id));req.setAttribute("registrations",DAOFactory.registrations().findByAttendee(id));req.setAttribute("messages",DAOFactory.messages().findForAttendee(id).stream().limit(5).toList());forward(req,res,"attendee/dashboard.jsp");}
}
