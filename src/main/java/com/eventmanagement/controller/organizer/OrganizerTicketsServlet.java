package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/tickets")
public class OrganizerTicketsServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{long organizerId=currentUser(req).getId();req.setAttribute("tickets",DAOFactory.tickets().findByOrganizer(organizerId));req.setAttribute("events",DAOFactory.events().findByOrganizer(organizerId));forward(req,res,"organizer/tickets.jsp");}
}
