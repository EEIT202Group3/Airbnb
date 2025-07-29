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

@WebServlet("/createOrder")
public class orderPreview extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public orderPreview() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {

		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();
		
		String customer_id = request.getParameter("customer_id");
		String houseName = request.getParameter("houseName");
		String address = request.getParameter("address");
		String tel = request.getParameter("tel");
		String bed = request.getParameter("bed");
		String checkin_date = request.getParameter("checkin_date");
		String checkout_date = request.getParameter("checkout_date");
		String people = request.getParameter("people");
		Integer price = Integer.parseInt(request.getParameter("price"));
		String days = request.getParameter("days");
		String total = request.getParameter("total");

		request.setAttribute("customer_id", customer_id);
		request.setAttribute("houseName", houseName);
		request.setAttribute("address", address);
		request.setAttribute("tel", tel);
		request.setAttribute("bed", bed);
		request.setAttribute("checkin_date", checkin_date);
		request.setAttribute("checkout_date", checkout_date);
		request.setAttribute("people", people);
		request.setAttribute("price", price);
		request.setAttribute("days", days);
		request.setAttribute("total", total);
		try {
			request.getRequestDispatcher("/JSP/payMent.jsp").forward(request, response);
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
