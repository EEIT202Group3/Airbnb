package com.EEITG3.Airbnb.listing.repository;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.payMent.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

import java.time.LocalDateTime;

public class LisBeanSpecifications {

    // 已審核且上架
    public static Specification<LisBean> isApprovedAndPublished() {
        return (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("approved"), true),
                        cb.equal(root.get("published"), true)
                );
    }

    // 地點模糊查詢（地址中包含關鍵字）
    public static Specification<LisBean> locationLike(String location) {
        return (root, query, cb) -> {
            if (location == null || location.trim().isEmpty()) {
                return cb.conjunction(); // 不加條件
            }
            return cb.like(cb.lower(root.get("ads")), "%" + location.toLowerCase() + "%");
        };
    }

    // 人數查詢（ppl >= guestCount）
    public static Specification<LisBean> guestCountAtLeast(Integer guestCount) {
        return (root, query, cb) -> {
            if (guestCount == null) {
                return cb.conjunction(); // 不加條件
            }
            return cb.ge(root.get("ppl"), guestCount);
        };
    }

    // 可入住日期查詢：排除在指定日期區間內已有訂單的房源
    public static Specification<LisBean> availableBetween(LocalDateTime checkIn, LocalDateTime checkOut) {
        return (root, query, cb) -> {
            if (checkIn == null || checkOut == null) {
                return cb.conjunction(); // 沒填日期就不限制
            }

            // 子查詢：找出在指定期間有訂單的 listId
            Subquery<Integer> subQuery = query.subquery(Integer.class);
            Root<Order> orderRoot = subQuery.from(Order.class);

            subQuery.select(orderRoot.get("listing").get("listId"))
                    .where(
                            cb.and(
                                    cb.lessThan(orderRoot.get("checkinDate"), checkOut),
                                    cb.greaterThan(orderRoot.get("checkoutDate"), checkIn)
                            )
                    );

            // 排除有衝突的房源
            return cb.not(root.get("listId").in(subQuery));
        };
    }
}
