package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/events")
public class OrganizerEventsServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("events",DAOFactory.events().findByOrganizer(currentUser(req).getId()));forward(req,res,"organizer/events.jsp");}
}
