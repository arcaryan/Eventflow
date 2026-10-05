package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/dashboard")
public class OrganizerDashboardServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{long id=currentUser(req).getId();req.setAttribute("stats",DAOFactory.events().organizerStats(id));req.setAttribute("events",DAOFactory.events().findByOrganizer(id));req.setAttribute("tickets",DAOFactory.tickets().findByOrganizer(id));req.setAttribute("messages",DAOFactory.messages().findByOrganizer(id).stream().limit(5).toList());forward(req,res,"organizer/dashboard.jsp");}
}
