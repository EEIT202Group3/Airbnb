package com.payMent.dao;



import java.time.LocalDate;
import java.util.List;


import org.hibernate.Session;
import org.hibernate.query.Query;

import com.payMent.bean.OrderBean;

import com.utils.HibernateUtil;



public class OrderDAO {
	
	private Session session;
	
	public OrderDAO(Session session) {
		this.session = session;
	}
	
	//新增訂單
	
	public void insertOrder(OrderBean orderBean){
		session.persist(orderBean); }
	//尋找單筆
	
	public OrderBean getById(String booking_id) {
		return session.find(OrderBean.class, booking_id);
	}
	
	//查詢全部
	
	public List<OrderBean> getByAll(String customerId) {
		String sql = "SELECT o FROM orderBean o";
		Query<OrderBean> query = session.createQuery(sql, OrderBean.class);
		return query.getResultList();
	}
	
	//日期邏輯
	public boolean isRoomBooked(String house_name, LocalDate checkin, LocalDate checkout) {
	    String sql = "SELECT COUNT(*) FROM orderlist " +
	                 "WHERE house_name = ? AND " +
	                 "((checkin_date < ? AND checkout_date > ?) OR " +
	                 "(checkin_date >= ? AND checkin_date < ?))";
	    
	    try(Session session = HibernateUtil.getSessionFactory().openSession()){
	    	Query<Long> hql = session.createQuery(sql, Long.class);
	        hql.setParameter("house_name", house_name);
	        hql.setParameter("checkin", checkin);
	        hql.setParameter("checkout", checkout);

	        Long count = hql.getSingleResult();
	        return count > 0;
	    }
	}
	
}		
