package com.payMent.dao;



import java.time.LocalDate;

import java.util.List;



import org.hibernate.Session;
import org.hibernate.query.Query;

import com.payMent.bean.OrderBean;

import com.utils.HibernateUtil;


public class adminDAO {
private Session session;
	
	public adminDAO(Session session) {
		this.session = session;
	}
	
	// 查詢單筆，依據訂單查詢單筆資料
	public OrderBean getById(String booking_id) {
		return session.find(OrderBean.class, booking_id);
	}
	
	//修改付款狀
	public boolean updatePaymentStatus(String booking_id, String ment_status) {
		String sql = "UPDATE orderlist SET ment_status=? WHERE booking_id=?";
	    OrderBean orderBean = session.find(OrderBean.class, booking_id);
        orderBean.setMent_status(ment_status);
        return false;
	}
	
	
	//修改付款狀態
		public boolean updateBookingPaymentStatus(String booking_id, String booking_status) {
		   String sql = "UPDATE orderlist SET booking_status=? WHERE booking_id=?";
		   OrderBean orderBean = session.find(OrderBean.class, booking_id);
		   orderBean.setBooking_status(booking_status);
		   return false;
		}
	
	// 查詢全部
		public List<OrderBean> getByAll(String customerId) {
			String sql = "SELECT o FROM orderBean o";
			Query<OrderBean> query = session.createQuery(sql, OrderBean.class);
			return query.getResultList();
		}
	
		//日期邏輯
		public boolean isRoomBooked(String house_name, LocalDate checkin, LocalDate checkout) {
		    String hql = "SELECT COUNT(*) FROM OrderBean " +
		                 "WHERE houseName = :house_name AND " +
		                 "((checkin_date < :checkout AND checkout_date > :checkin) OR " +
		                 "(checkin_date >= :checkin AND checkin_date < :checkout))";

		    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
		        Query<Long> query = session.createQuery(hql, Long.class);
		        query.setParameter("house_name", house_name);
		        query.setParameter("checkin", checkin);
		        query.setParameter("checkout", checkout);

		        Long count = query.getSingleResult();
		        return count > 0;
		    }
		}
		
	
}
		
