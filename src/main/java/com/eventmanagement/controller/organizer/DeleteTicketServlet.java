package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/tickets/delete")
public class DeleteTicketServlet extends BaseServlet {
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{try{DAOFactory.tickets().delete(requiredId(req),currentUser(req).getId());ServletUtil.flash(req,"success","Ticket type deleted.");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());}redirect(req,res,"/organizer/tickets");}
}
