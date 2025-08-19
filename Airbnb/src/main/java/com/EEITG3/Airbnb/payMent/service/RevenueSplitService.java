package com.EEITG3.Airbnb.payMent.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.payMent.entity.Order;

/**
 * 專責拆帳：把平台抽成、稅、折扣、房東淨收、結算月份等欄位一次寫回訂單
 * 先用 15% 抽成、稅與折扣為 0；之後可改為從房東方案/平台設定載入
 */
@Service
public class RevenueSplitService {

    // 之後可抽到 DB/設定（依 hostId / listing 做差異費率）
    private static final BigDecimal DEFAULT_FEE_RATE = new BigDecimal("0.20");

    public void applySplitAndPersist(Order order) {
        if (order == null) return;

        BigDecimal grandtotal = order.getGrandTotal() != null ? order.getGrandTotal() : BigDecimal.ZERO;

        BigDecimal feeRate = DEFAULT_FEE_RATE;
        BigDecimal fee = grandtotal.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal hostNet = grandtotal.subtract(fee).setScale(2, RoundingMode.HALF_UP);

        order.setGrandTotal(grandtotal);
        order.setPlatformFeeRate(feeRate);
        order.setPlatformFeeAmount(fee);
        order.setHostNetAmount(hostNet);

        // 建議用「退房月 + 1」作為結算月份，避免提前結算未入住訂單
        if (order.getCheckoutDate() != null) {
            YearMonth settleMonth = YearMonth.from(order.getCheckoutDate());
            order.setSettlementMonth(settleMonth.toString()); // "YYYY-MM"
        } else {
            // 若沒有退房日，先放當月（可依你業務調整）
            order.setSettlementMonth(YearMonth.now().toString());
        }

        // 撥款與收入狀態（可隨退款/爭議在其他流程調整）
        order.setPayoutStatus("pending");
        order.setRevenueStatus("confirmed");
    }
}