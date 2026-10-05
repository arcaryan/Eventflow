package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.model.Event;
import com.eventmanagement.service.EventService;
import com.eventmanagement.util.ServletUtil;
import com.eventmanagement.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/events/create")
public class CreateEventServlet extends BaseServlet {
    private final EventService service=new EventService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{forward(req,res,"organizer/event-form.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{Event e=read(req);e.setOrganizerId(currentUser(req).getId());service.create(e);ServletUtil.flash(req,"success","Event saved as draft.");redirect(req,res,"/organizer/events");}catch(RuntimeException e){req.setAttribute("error",e.getMessage());req.setAttribute("form",req.getParameterMap());forward(req,res,"organizer/event-form.jsp");}}
    protected Event read(HttpServletRequest req){Event e=new Event();e.setTitle(ValidationUtil.required(req.getParameter("title"),"Title"));e.setDescription(ValidationUtil.required(req.getParameter("description"),"Description"));e.setCategory(req.getParameter("category"));e.setEventDate(ValidationUtil.date(req.getParameter("eventDate"),"Event date"));e.setStartTime(ValidationUtil.time(req.getParameter("startTime"),"Start time"));e.setEndTime(ValidationUtil.time(req.getParameter("endTime"),"End time"));e.setVenue(ValidationUtil.required(req.getParameter("venue"),"Venue"));e.setCapacity(ValidationUtil.positiveInt(req.getParameter("capacity"),"Capacity"));e.setBannerUrl(req.getParameter("bannerUrl"));return e;}
}
