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
import com.payMent.dao.adminDAO;

@WebServlet("/adminOrderList")
public class adminOrderList extends HttpServlet {

	public adminOrderList() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();

		String customer_id = (String) request.getSession().getAttribute("customerId");
			if (customer_id == null) {
				throw new IllegalArgumentException("Session中找不到customer_id，請先登入");
			}
		System.out.println("customer_id from session = " + customer_id);
		adminDAO dao1 = new adminDAO(session);
		List<Order> orders = dao1.getByAll(customer_id);
		request.setAttribute("orders", orders);
		request.getRequestDispatcher("/JSP/adminOrderList.jsp").forward(request, response);
		System.out.println("抓到筆數：" + orders.size());

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
