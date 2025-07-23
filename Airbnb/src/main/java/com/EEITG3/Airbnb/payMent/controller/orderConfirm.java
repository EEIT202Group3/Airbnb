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
import java.util.UUID;



import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.listing.bean.LisBean;
import com.listing.dao.ListDao;

@WebServlet("/orderConfirm")
public class orderConfirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public orderConfirm() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
	    try {
	        String listIdStr = request.getParameter("listId");
	        Integer listId = Integer.parseInt(listIdStr);

	        // 改這裡：從表單拿值（如果你沒用 session）
	        String customerId = request.getParameter("customerId");

	        if (customerId == null || customerId.isBlank()) {
	            response.sendRedirect(request.getContextPath() + "/error.jsp");
	            return;
	        }

	        SessionFactory factory = HibernateUtil.getSessionFactory();
	        Session session = factory.getCurrentSession();
	        ListDao dao = new ListDao();
	        CustomerDAO cdao = new CustomerDAO(session);

	        String checkin_date = request.getParameter("checkin_date");
	        String checkout_date = request.getParameter("checkout_date");

	        LisBean listing = dao.findListBeanById(listId);
	        CustomerBean customer = cdao.findById(customerId);

	        request.setAttribute("listing", listing);
	        request.setAttribute("customer", customer);
	        request.setAttribute("checkin_date", checkin_date);
	        request.setAttribute("checkout_date", checkout_date);

	        request.getRequestDispatcher("/JSP/orderConfirm.jsp").forward(request, response);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
		
}