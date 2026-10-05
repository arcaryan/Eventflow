package com.eventmanagement.controller.attendee;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/attendee/updates")
public class AttendeeUpdatesServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("messages",DAOFactory.messages().findForAttendee(currentUser(req).getId()));forward(req,res,"attendee/updates.jsp");}
}
