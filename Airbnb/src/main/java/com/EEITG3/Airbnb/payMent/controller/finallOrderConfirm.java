package com.payMent.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.utils.HibernateUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.payMent.bean.OrderBean;
import com.payMent.dao.OrderDAO;

@WebServlet("/finallOrderConfirm")
public class finallOrderConfirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public finallOrderConfirm() {
		super();
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
        	SessionFactory factory = HibernateUtil.getSessionFactory();
	    	Session session = factory.getCurrentSession();
        	
            // 從表單拿資料
	    	String customer_id = request.getParameter("custormer_id");
            System.out.println("customer_id param = " + customer_id);

            String lisidStr = request.getParameter("lisid");
            int lisid = Integer.parseInt(lisidStr);

            String username = request.getParameter("username");
            String house_name = request.getParameter("house_name");
            String address = request.getParameter("address");
            String tel = request.getParameter("tel");
            String bed = request.getParameter("bed");
            String checkin_date = request.getParameter("checkin_date");
            String checkout_date = request.getParameter("checkout_date");
            int people = Integer.parseInt(request.getParameter("people"));
            int price = Integer.parseInt(request.getParameter("price"));
            int days = Integer.parseInt(request.getParameter("days"));
            int totalInt = Integer.parseInt(request.getParameter("total"));

            String payment_id = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();
            String booking_method = "現金";
            String ment_status = "待付款";
            String booking_status = "待入住";

            if ("信用卡".equals(booking_method)) {
                ment_status = "已付款";
            }

            LocalDateTime checkoutDateTime = LocalDateTime.parse(checkout_date + "T00:00:00");
            if (checkoutDateTime.isBefore(now)) {
                booking_status = "已退房";
            }

            OrderBean bean = new OrderBean();
            bean.setLisid(lisid);
            bean.setCustomer_id(customer_id);
            bean.setReservation_id(0);
            bean.setUsername(username);
            bean.setHouse_name(house_name);
            bean.setAddress(address);
            bean.setTel(tel);
            bean.setBed(bed);
            bean.setPeople(people);
            bean.setCheckin_date(Timestamp.valueOf(checkin_date + " 00:00:00"));
            bean.setCheckout_date(Timestamp.valueOf(checkout_date + " 00:00:00"));
            bean.setLocation_id(0);
            bean.setCreated_time(Timestamp.valueOf(now));
            bean.setBooking_status(booking_status);
            bean.setPayment_id(payment_id);
            bean.setPrice(price);
            bean.setTotal_amount(BigDecimal.valueOf(price * days));
            bean.setTotal(BigDecimal.valueOf(totalInt));
            bean.setPaid_time(Timestamp.valueOf(now));
            bean.setBooking_method(booking_method);
            bean.setMent_status(ment_status);

            OrderDAO dao = new OrderDAO(session);
            dao.insertOrder(bean);

            // 回傳結果
            request.setAttribute("username", username);
            request.setAttribute("house_name", house_name);
            request.setAttribute("address", address);
            request.setAttribute("tel", tel);
            request.setAttribute("bed", bed);
            request.setAttribute("checkin_date", checkin_date);
            request.setAttribute("checkout_date", checkout_date);
            request.setAttribute("people", people);
            request.setAttribute("price", price);
            request.setAttribute("days", days);
            request.setAttribute("total", totalInt);
            request.getRequestDispatcher("/JSP/order.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(500, "訂單新增失敗：" + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}