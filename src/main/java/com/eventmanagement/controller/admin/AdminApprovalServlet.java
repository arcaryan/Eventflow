package com.eventmanagement.controller.admin;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.model.EventStatus;
import com.eventmanagement.util.ServletUtil;
import com.eventmanagement.service.ActivityService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/events/pending")
public class AdminApprovalServlet extends BaseServlet {
    private final ActivityService activity = new ActivityService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("events",DAOFactory.events().findPending());forward(req,res,"admin/approvals.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{long id=requiredId(req);String action=req.getParameter("action");EventStatus status="approve".equals(action)?EventStatus.APPROVED:EventStatus.REJECTED;DAOFactory.events().updateStatus(id,status, "reject".equals(action)?req.getParameter("reason"):null);activity.log(currentUser(req).getId(),"EVENT_"+status.name(),"EVENT",id,"Admin changed event approval status to "+status.name()+".");ServletUtil.flash(req,"success","Event status updated.");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());}redirect(req,res,"/admin/events/pending");}
}
