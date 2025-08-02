package com.EEITG3.Airbnb.carRent.service;//package com.airbnb.airbnb.service;
//
//import com.CarRent.Util.HibernateUtil;
//import com.CarRent.dao.ReservationDAO;
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
//import java.util.List;
//
//@WebServlet("/SearchVehicleServlet")
//public class SearchVehicleServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        String location = request.getParameter("location");
//        String pickupDate = request.getParameter("pickupDate");
//        String pickupTime = request.getParameter("pickupTime");
//        String returnDate = request.getParameter("returnDate");
//        String returnTime = request.getParameter("returnTime");
//        String ageCheck = request.getParameter("ageCheck");
//        try {
//            if (location == null || location.trim().isEmpty()) {
//                request.setAttribute("error", "請選擇取車地點");
//                request.getRequestDispatcher("CarRentJsp/homepage.jsp").forward(request, response);
//                return;
//            }
//            LocalDateTime pickupDateTime = LocalDateTime.parse(pickupDate + "T" + pickupTime);
//            LocalDateTime returnDateTime = LocalDateTime.parse(returnDate + "T" + returnTime);
//            ReservationDAO reservationDAO = new ReservationDAO(session);
//            List<Vehicle> rentableVehicle = reservationDAO.findRentableVehicle(pickupDateTime, returnDateTime);
//            request.setAttribute("rentableVehicle", rentableVehicle);
//            request.getRequestDispatcher("CarRentJsp/searchResult.jsp").forward(request, response);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            request.setAttribute("error", "請確認時間格式正確");
//            request.getRequestDispatcher("CarRentJsp/homepage.jsp").forward(request, response);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        doGet(request, response);
//    }
//}