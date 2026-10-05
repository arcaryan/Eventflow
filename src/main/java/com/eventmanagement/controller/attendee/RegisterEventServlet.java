package com.eventmanagement.controller.attendee;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.service.RegistrationService;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/attendee/register-event")
public class RegisterEventServlet extends BaseServlet {
    private final RegistrationService service=new RegistrationService();
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{try{service.register(requiredId(req),currentUser(req).getId());ServletUtil.flash(req,"success","You are registered for this event.");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());}redirect(req,res,"/attendee/events");}
}
