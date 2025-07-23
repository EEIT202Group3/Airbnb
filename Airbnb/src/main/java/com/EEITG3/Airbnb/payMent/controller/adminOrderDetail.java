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
import com.payMent.dao.adminDAO;



@WebServlet("/adminOrderDetail")
public class adminOrderDetail extends HttpServlet {
	public adminOrderDetail() {
		super();
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
    	
    	SessionFactory factory = HibernateUtil.getSessionFactory();
    	Session session = factory.getCurrentSession();
        String booking_id = request.getParameter("booking_id");

        // 查單筆
        if (booking_id != null && !booking_id.isEmpty()) {
            adminDAO dao = new adminDAO(session);
            OrderBean order = dao.getById(booking_id);
            request.setAttribute("order", order);
            request.getRequestDispatcher("/JSP/adminOrderDetail.jsp").forward(request, response);
        } else {
            // 沒有ID直接回列表
            response.sendRedirect(request.getContextPath() + "/adminOrderList");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
    	SessionFactory factory = HibernateUtil.getSessionFactory();
    	Session session = factory.getCurrentSession();
        String booking_id = request.getParameter("booking_id");
        String ment_status = request.getParameter("ment_status");
        String booking_status = request.getParameter("booking_status");

        adminDAO dao = new adminDAO(session);
        boolean success = false;

        // 只更新有傳入的參數
        if (ment_status != null && !ment_status.isEmpty()) {
            success = dao.updatePaymentStatus(booking_id, ment_status);;
        } else if (booking_status != null && !booking_status.isEmpty()) {
            success = dao.updateBookingPaymentStatus(booking_id, booking_status);
        }

        // 回到明細頁（查詢最新資料）
        if (success) {
            response.sendRedirect(request.getContextPath() + "/adminOrderDetail?booking_id=" + booking_id + "&msg=updateSuccess");
        } else {
            response.sendRedirect(request.getContextPath() + "/adminOrderDetail?booking_id=" + booking_id + "&msg=updateFail");
        }
    
}
}


