package com.studentcourse.controller;

import com.studentcourse.dao.RegistrationDAO;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class UpdateRegistrationStatusServlet extends HttpServlet {
    private RegistrationDAO registrationDAO;

    @Override
    public void init() throws ServletException {
        registrationDAO = new RegistrationDAO();
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get form parameters
        String registrationIdStr = request.getParameter("registrationId");
        String status = request.getParameter("status");

        // Validation
        String error = "";
        if (registrationIdStr == null || registrationIdStr.isEmpty()) {
            error = "Invalid registration ID";
        } else if (status == null || status.isEmpty()) {
            error = "Status must be selected";
        } else {
            if (!status.equals("Active") && !status.equals("Completed") && !status.equals("Cancelled")) {
                error = "Invalid status";
            }
        }

        if (!error.isEmpty()) {
            request.setAttribute("error", error);
            request.setAttribute("registrations", registrationDAO.getAllRegistrations());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registration-list.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Update status
        try {
            int registrationId = Integer.parseInt(registrationIdStr);
            
            if (registrationDAO.updateRegistrationStatus(registrationId, status)) {
                // Successful update - use sendRedirect
                response.sendRedirect(request.getContextPath() + "/registrations");
            } else {
                response.sendRedirect(request.getContextPath() + "/registrations");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/registrations");
        }
    }
}
