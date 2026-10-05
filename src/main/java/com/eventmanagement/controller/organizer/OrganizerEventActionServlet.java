package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.model.EventStatus;
import com.eventmanagement.service.EventService;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet({"/organizer/events/delete","/organizer/events/submit","/organizer/events/cancel"})
public class OrganizerEventActionServlet extends BaseServlet {
    private final EventService service=new EventService();
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{try{long id=requiredId(req);String path=req.getServletPath();if(path.endsWith("delete"))service.deleteDraft(id,currentUser(req).getId());else if(path.endsWith("submit"))service.submit(id,currentUser(req).getId());else service.cancel(id,currentUser(req).getId());ServletUtil.flash(req,"success","Event action completed.");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());}redirect(req,res,"/organizer/events");}
}
