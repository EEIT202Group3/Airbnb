package com.payMent.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.utils.HibernateUtil;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.payMent.bean.OrderBean;
import com.payMent.dao.OrderDAO;

@WebServlet("/getOrderDetail")
public class getOrderDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public getOrderDetail() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();
		OrderDAO dao = new OrderDAO(session);

		String booking_id = request.getParameter("booking_id");

		OrderBean order = dao.getById(booking_id);

		request.setAttribute("order", order);
		try {
			request.getRequestDispatcher("/JSP/getOrderDetail.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
