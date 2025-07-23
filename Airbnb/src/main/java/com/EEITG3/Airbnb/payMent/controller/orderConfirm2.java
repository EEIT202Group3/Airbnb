package com.payMent.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.users.bean.CustomerBean;
import com.users.dao.CustomerDAO;
import com.utils.HibernateUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;



import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.listing.bean.LisBean;
import com.listing.dao.ListDao;
import com.payMent.dao.OrderDAO;

@WebServlet("/orderConfirm2")
public class orderConfirm2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public orderConfirm2() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
	    try {
	    
	    	SessionFactory factory = HibernateUtil.getSessionFactory();
	    	Session session = factory.getCurrentSession();
	    	ListDao dao = new ListDao();
	    	CustomerDAO cdao = new CustomerDAO(session);
	    	
	        // 先拿 listId
	        String listIdStr = request.getParameter("listId");
//	        //測試有沒有收到值
//	        if (listIdStr == null || listIdStr.isBlank()) {
//	            throw new IllegalArgumentException("listId 為空，請確認表單有正確傳入");
//	        }
	        Integer listId = Integer.parseInt(listIdStr);

			
	        // 從 Session 拿 customerId
	        String customer_id = (String) request.getSession().getAttribute("customerId");
	        UUID customerId = UUID.fromString(customer_id);
	       

	        String checkin_date = request.getParameter("checkin_date");
	        String checkout_date = request.getParameter("checkout_date");
	        int people = Integer.parseInt(request.getParameter("people"));
	        int price = Integer.parseInt(request.getParameter("price"));

	        // 撈房源資料
	        LisBean listing = dao.findListBeanById(listId);

	        // 撈會員資料
	        CustomerBean customer = cdao.findById(customerId.toString());

	        request.setAttribute("listing", listing);
	        request.setAttribute("customer", customer);

	        LocalDate checkin = LocalDate.parse(checkin_date);
	        LocalDate checkout = LocalDate.parse(checkout_date);
	        long days = ChronoUnit.DAYS.between(checkin, checkout);

	        OrderDAO daoo = new OrderDAO(session);
	        boolean isBooked = daoo.isRoomBooked(listing.getHouse_name(), checkin, checkout);
	        if (isBooked) {
	            request.setAttribute("error", "此房間在您選擇的日期已被預訂，請選擇其他日期！");
	            request.getRequestDispatcher("/JSP/orderError.jsp").forward(request, response);
	            return;
	        }

	        int total = price * (int) days * people / 2;
	        request.setAttribute("checkin_date", checkin_date);
	        request.setAttribute("checkout_date", checkout_date);
	        request.setAttribute("people", people);
	        request.setAttribute("days", days);
	        request.setAttribute("price", price);
	        request.setAttribute("total", total);

	        System.out.println("listing: " + listing);
	        System.out.println("customer: " + customer);

	        request.getRequestDispatcher("/JSP/orderPreview.jsp").forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }
	}

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}