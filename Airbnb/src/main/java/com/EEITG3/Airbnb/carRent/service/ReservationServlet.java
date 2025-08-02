package com.EEITG3.Airbnb.carRent.service;//package com.airbnb.airbnb.service;
//
//import com.CarRent.Util.HibernateUtil;
//import com.CarRent.dao.ReservationDAO;
//import com.CarRent.model.Reservation;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.hibernate.Session;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//
//@WebServlet("/ReservationServlet")
//public class ReservationServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        doPost(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        String firstname = request.getParameter("firstname");
//        String lastname = request.getParameter("lastname");
//        String name = firstname + lastname;
//        String email = request.getParameter("email");
//        String phone = request.getParameter("phone");
//        String pickupParam = request.getParameter("pickupDateTime");
//        String returnParam = request.getParameter("returnDateTime");
//        String license  = request.getParameter("license");
//        if (pickupParam == null || returnParam == null) {
//            throw new RuntimeException("時間參數遺失");
//        }
//        LocalDateTime pickupDateTime = LocalDateTime.parse(pickupParam);
//        LocalDateTime returnDateTime = LocalDateTime.parse(returnParam);
//        Reservation r = new Reservation();
//
//        //有測試用資料 要改
//        try {
//            r.setDriverName(name);
//            r.setDriverEmail(email);
//            r.setDriverPhone(phone);
//            r.setPickupDate(pickupDateTime);
//            r.setReturnDate(returnDateTime);
//            r.setDeposit(BigDecimal.ZERO);
//            r.setDriverAge("20");
//            r.setTotalAmount(BigDecimal.valueOf(5060));
//            r.setStatus("預約中");
//            r.setLicense(license);
//            r.setCreatedAt(LocalDateTime.now());
//            r.setCurrentDiscount("NT$290");
//            r.setVehicleId(1);
//            r.setCustomerId(1);
//            r.setLocationId(1);
//            ReservationDAO reservationDAO = new ReservationDAO(session);
//            reservationDAO.insertReservation(r);
//            response.sendRedirect("CarRentJsp/success.jsp");
//        } catch (Exception e) {
//            response.sendRedirect("CarRentJsp/error.jsp");
//        }
//    }
//}