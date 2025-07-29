package com.EEITG3.Airbnb.payMent.repository;



import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.EEITG3.Airbnb.payMent.entity.Order;

@Repository
public class OrderRepository2 {
	

    private final SessionFactory sessionFactory;

    @Autowired
    public OrderRepository2(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    //新增訂單
    public Order save(Order order) {
        getSession().persist(order);
        return order;
    }
    //查詢單筆
    public Order getById(String booking_id) {
        return getSession().find(Order.class, booking_id);
    }

    //依會員查詢全部訂單
    public List<Order> getByCustomer(String customerId) {
        String hql = "FROM Order o WHERE o.customer.customerId = :customerId";
        Query<Order> query = getSession().createQuery(hql, Order.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }
    //日期判斷邏輯
    public boolean isRoomBooked(String house_name, LocalDateTime checkin_date, LocalDateTime checkout_date) {
        String hql = "SELECT COUNT(o) FROM Order o " +
                     "WHERE o.houseName = :houseName AND " +
                     "((o.checkinDate < :checkout AND o.checkoutDate > :checkin))";

        Query<Long> query = getSession().createQuery(hql, Long.class);
        query.setParameter("houseName", house_name);
        query.setParameter("checkin", checkin_date);
        query.setParameter("checkout", checkout_date);

        Long count = query.getSingleResult();
        return count > 0;
    }
}