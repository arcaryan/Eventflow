package com.eventmanagement.controller;

import com.eventmanagement.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException, jakarta.servlet.ServletException{req.setAttribute("events",DAOFactory.events().findApproved("",""));forward(req,res,"public/home.jsp");}
}
