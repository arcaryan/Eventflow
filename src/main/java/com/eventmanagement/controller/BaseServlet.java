package com.eventmanagement.controller;

import com.eventmanagement.model.User;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void forward(HttpServletRequest request,HttpServletResponse response,String view)throws ServletException,IOException{ServletUtil.exposeFlash(request);request.getRequestDispatcher("/WEB-INF/views/"+view).forward(request,response);}
    protected void redirect(HttpServletRequest request,HttpServletResponse response,String location)throws IOException{response.sendRedirect(request.getContextPath()+location);}
    protected User currentUser(HttpServletRequest request){return ServletUtil.currentUser(request);}
    protected long requiredId(HttpServletRequest request){try{return Long.parseLong(request.getParameter("id"));}catch(Exception e){throw new IllegalArgumentException("A valid id is required.");}}
    protected void error(HttpServletRequest request,HttpServletResponse response,String message)throws ServletException,IOException{request.setAttribute("error",message);forward(request,response,"common/error.jsp");}
}
