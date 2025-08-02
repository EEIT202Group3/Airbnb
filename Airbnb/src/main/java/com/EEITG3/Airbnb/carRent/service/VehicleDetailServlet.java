package com.EEITG3.Airbnb.carRent.service;//package com.airbnb.airbnb.service;
//
//import com.CarRent.Util.HibernateUtil;
//import com.CarRent.dao.VehicleDAO;
//import com.CarRent.model.Vehicle;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.hibernate.Session;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//
//@WebServlet("/VehicleDetailServlet")
//public class VehicleDetailServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//
//        try {
//            int vehicleId = Integer.parseInt(request.getParameter("id"));
//            String pickupDate = request.getParameter("pickupDate");
//            String pickupTime = request.getParameter("pickupTime");
//            String returnDate = request.getParameter("returnDate");
//            String returnTime = request.getParameter("returnTime");
//            String license = request.getParameter("license");
//            String image = request.getParameter("image");
//            LocalDateTime pickupDateTime = LocalDateTime.parse(pickupDate + "T" + pickupTime);
//            LocalDateTime returnDateTime = LocalDateTime.parse(returnDate + "T" + returnTime);
//            VehicleDAO dao = new VehicleDAO(session);
//            Vehicle vehicle = dao.findVehicleById(vehicleId);
//            if (vehicle == null) {
//                request.setAttribute("error", "查無此車輛");
//                request.getRequestDispatcher("/CarRentJsp/vehicleDetail.jsp").forward(request, response);
//                return;
//            }
//            request.setAttribute("vehicle", vehicle);
//            request.setAttribute("pickupDateTime", pickupDateTime);
//            request.setAttribute("returnDateTime", returnDateTime);
//            request.setAttribute("pickupDateTimeStr", pickupDateTime.toString());
//            request.setAttribute("returnDateTimeStr", returnDateTime.toString());
//            request.setAttribute("image", image);
//            request.setAttribute("license", license);
//            request.getRequestDispatcher("/CarRentJsp/vehicleDetail.jsp").forward(request, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendRedirect("CarRentJsp/searchResult.jsp");
//        }
//    }
//}