package com.payMent.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.utils.HibernateUtil;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.payMent.bean.OrderBean;
import com.payMent.dao.OrderDAO;



@WebServlet("/getOrderList")
public class getOrderList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public getOrderList() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		SessionFactory factory = HibernateUtil.getSessionFactory();
    	Session session = factory.getCurrentSession();
    	OrderDAO dao = new OrderDAO(session);
		
		
	    String customer_id = (String) request.getSession().getAttribute("customerId");
	    if (customer_id == null) {
	        throw new IllegalArgumentException("Session中找不到customer_id，請先登入");
	    }
	    System.out.println("customer_id from session = " + customer_id);

	    List<OrderBean> orders = dao.getByAll(customer_id);
	    request.setAttribute("orders", orders);

	    try {
	        request.getRequestDispatcher("/JSP/getOrderList.jsp").forward(request, response);
	    } catch (ServletException | IOException e) {
	        e.printStackTrace();
	    }
	    System.out.println("抓到筆數：" + orders.size());
	}
}

