package com.EEITG3.Airbnb.payMent.service;

import com.EEITG3.Airbnb.payMent.dto.PayoutPreview;
import com.EEITG3.Airbnb.payMent.dto.PayoutRow;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;   
import java.util.UUID;

@Service
public class PayoutService {

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public PayoutService(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    @Transactional(readOnly = true)
    public PayoutPreview preview(YearMonth month) {
        String ym = month.toString(); // "YYYY-MM"
        String sql = """
            SELECT host_id,
                   :ym AS payout_month,
                   SUM(total_amount)        AS total_earnings,
                   SUM(platform_fee_amount) AS total_platform_fee,
                   SUM(host_net_amount)     AS total_net_payout,
                   COUNT(*)                 AS orders
            FROM orderlist
            WHERE FORMAT(checkout_date,'yyyy-MM') = :ym
              AND booking_status = N'已完成'
              AND ment_status    = N'已付款'
              AND revenue_status = N'confirmed'
              AND payout_status  = N'pending'
            GROUP BY host_id
            ORDER BY host_id
        """;

        var rows = namedJdbc.query(sql, Map.of("ym", ym), (rs, i) -> new PayoutRow(
            rs.getString("host_id"),
            rs.getString("payout_month"),
            rs.getBigDecimal("total_earnings"),
            rs.getBigDecimal("total_platform_fee"),
            rs.getBigDecimal("total_net_payout"),
            rs.getInt("orders")
        ));
        return new PayoutPreview(ym, rows);
    }

    @Transactional
    public void generateAndLockPayouts(YearMonth month, String adminUser) {
        String ym = month.toString();

        var preview = preview(month);
        for (PayoutRow row : preview.getRows()) {   
            UUID payoutId;
            try {
                payoutId = jdbc.queryForObject("""
                    INSERT INTO host_payouts (payout_id, host_id, payout_month,
                                              total_earnings, total_platform_fee, total_net_payout,
                                              status, created_at, updated_at)
                    OUTPUT inserted.payout_id
                    VALUES (NEWID(), ?, ?, ?, ?, ?, 'generated', SYSUTCDATETIME(), SYSUTCDATETIME())
                """,
                    (rs, i) -> rs.getObject(1, java.util.UUID.class),
                    row.getHostId(), ym, row.getTotalEarnings(),
                    row.getTotalPlatformFee(), row.getTotalNetPayout()
                );
            } catch (DataIntegrityViolationException dup) {
                payoutId = jdbc.queryForObject("""
                    SELECT TOP 1 payout_id FROM host_payouts
                    WHERE host_id = ? AND payout_month = ?
                """,
                    (rs, i) -> rs.getObject(1, java.util.UUID.class),
                    row.getHostId(), ym
                );
            }

            jdbc.update("""
                INSERT INTO payout_orders (payout_order_id, payout_id, booking_id, list_id,
                                           gross_amount, platform_fee, net_amount, created_at)
                SELECT NEWID(), ?, o.booking_id, o.list_id,
                       o.total_amount, o.platform_fee_amount, o.host_net_amount,
                       SYSUTCDATETIME()
                FROM orderlist o
                WHERE FORMAT(o.checkout_date,'yyyy-MM') = ?
                  AND o.host_id = ?
                  AND o.booking_status = N'完成'
                  AND o.ment_status    = N'已付款'
                  AND o.revenue_status = N'confirmed'
                  AND o.payout_status  = N'pending'
            """, payoutId, ym, row.getHostId());

            jdbc.update("""
                UPDATE o SET
                    o.payout_status = 'scheduled',
                    o.payout_id = ?
                FROM orderlist o
                WHERE FORMAT(o.checkout_date,'yyyy-MM') = ?
                  AND o.host_id = ?
                  AND o.booking_status = N'完成'
                  AND o.ment_status    = N'已付款'
                  AND o.revenue_status = N'confirmed'
                  AND o.payout_status  = N'pending'
            """, payoutId, ym, row.getHostId());
        }
    }

    @Transactional
    public void markPayoutPaid(UUID payoutId, LocalDateTime paidAt) {
        jdbc.update("""
            UPDATE host_payouts
            SET status='paid', payout_date=?, updated_at=SYSUTCDATETIME()
            WHERE payout_id=?
        """, Timestamp.valueOf(paidAt), payoutId);

        jdbc.update("""
            UPDATE o SET o.payout_status='paid'
            FROM orderlist o
            JOIN payout_orders po ON po.booking_id = o.booking_id
            WHERE po.payout_id = ?
        """, payoutId);
    }
}