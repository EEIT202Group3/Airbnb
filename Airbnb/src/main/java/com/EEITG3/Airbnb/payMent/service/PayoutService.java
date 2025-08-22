package com.EEITG3.Airbnb.payMent.service;

import com.EEITG3.Airbnb.payMent.dto.HostPayoutDto;
import com.EEITG3.Airbnb.payMent.dto.PayoutOrderDto;
import com.EEITG3.Airbnb.payMent.dto.PayoutPreviewDto;
import com.EEITG3.Airbnb.payMent.dto.PayoutRowDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
    public PayoutPreviewDto preview(YearMonth month) {
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

        var rows = namedJdbc.query(sql, Map.of("ym", ym), (rs, i) -> new PayoutRowDto(
            rs.getString("host_id"),
            rs.getString("payout_month"),
            rs.getBigDecimal("total_earnings"),
            rs.getBigDecimal("total_platform_fee"),
            rs.getBigDecimal("total_net_payout"),
            rs.getInt("orders")
        ));
        return new PayoutPreviewDto(ym, rows);
    }

    @Transactional
    public void generateAndLockPayouts(YearMonth month, String adminUser) {
        String ym = month.toString();

        var preview = preview(month);
        for (PayoutRowDto row : preview.getRows()) {   
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
                  AND o.booking_status = N'已完成'
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
                  AND o.booking_status = N'已完成'
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
//    @Transactional
//    public void markPayoutCancel(UUID payoutId, LocalDateTime paidAt) {
//        jdbc.update("""
//            UPDATE host_payouts
//            SET status='paid', payout_date=?, updated_at=SYSUTCDATETIME()
//            WHERE payout_id=?
//        """, Timestamp.valueOf(paidAt), payoutId);
//
//        jdbc.update("""
//            UPDATE o SET o.payout_status='paid'
//            FROM orderlist o
//            JOIN payout_orders po ON po.booking_id = o.booking_id
//            WHERE po.payout_id = ?
//        """, payoutId);
//    }

    @Transactional(readOnly = true)
    public java.util.List<HostPayoutDto> findHostPayouts(String hostId, String month, String status) {
        StringBuilder sb = new StringBuilder("""
            SELECT hp.payout_id,
                   hp.host_id,
                   hp.payout_month,
                   hp.total_earnings,
                   hp.total_platform_fee,
                   hp.total_net_payout,
                   hp.status,
                   hp.payout_date,
                   hp.created_at,
                   hp.updated_at,
                   ISNULL(po.cnt, 0) AS orders
            FROM host_payouts hp
            OUTER APPLY (
                SELECT COUNT(*) AS cnt
                FROM payout_orders o
                WHERE o.payout_id = hp.payout_id
            ) po
            WHERE 1=1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (hostId != null && !hostId.isBlank()) {
            sb.append(" AND hp.host_id = :hostId");
            params.addValue("hostId", hostId);
        }
        if (month != null && !month.isBlank()) {
            sb.append(" AND hp.payout_month = :month");
            params.addValue("month", month);
        }
        if (status != null && !status.isBlank()) {
            sb.append(" AND hp.status = :status");
            params.addValue("status", status);
        }

        sb.append(" ORDER BY hp.payout_month DESC, hp.updated_at DESC");

        return namedJdbc.query(sb.toString(), params, (rs, i) -> new HostPayoutDto(
            rs.getObject("payout_id", java.util.UUID.class),
            rs.getString("host_id"),
            rs.getString("payout_month"),
            rs.getBigDecimal("total_earnings"),
            rs.getBigDecimal("total_platform_fee"),
            rs.getBigDecimal("total_net_payout"),
            rs.getString("status"),
            rs.getTimestamp("payout_date") != null ? rs.getTimestamp("payout_date").toLocalDateTime() : null,
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
            rs.getInt("orders")
        ));
    }

    /**
     * 依 payoutId 查詢該撥款單涵蓋的所有 payout_orders
     */
    @Transactional(readOnly = true)
    public java.util.List<PayoutOrderDto> findPayoutOrdersByPayoutId(java.util.UUID payoutId) {
        String sql = """
            SELECT payout_order_id, payout_id, booking_id, list_id,
                   gross_amount, platform_fee, net_amount, created_at
            FROM payout_orders
            WHERE payout_id = ?
            ORDER BY created_at DESC
        """;
        return jdbc.query(sql, (rs, i) -> new PayoutOrderDto(
            rs.getObject("payout_order_id", java.util.UUID.class),
            rs.getObject("payout_id", java.util.UUID.class),
            rs.getString("booking_id"),             
            (Integer) rs.getObject("list_id"),     
            rs.getBigDecimal("gross_amount"),
            rs.getBigDecimal("platform_fee"),
            rs.getBigDecimal("net_amount"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        ), payoutId);
    }
    
    @Transactional
    public void cancelPayout(UUID payoutId, String adminUser, String reason) {
        // 1) 取目前狀態並做防呆
        String status = jdbc.queryForObject(
            "SELECT status FROM host_payouts WHERE payout_id = ?",
            String.class, payoutId
        );
        if (status == null) {
            throw new IllegalArgumentException("找不到撥款單：" + payoutId);
        }
        if ("paid".equalsIgnoreCase(status)) {
            throw new IllegalStateException("已付款的撥款單不可取消");
        }
        if ("cancelled".equalsIgnoreCase(status)) {
            // 已經取消就直接返回（或丟錯都可）
            return;
        }

        // 2) 先把訂單恢復可再結算
        jdbc.update("""
            UPDATE o SET
                o.payout_status = 'pending',
                o.payout_id = NULL
            FROM orderlist o
            JOIN payout_orders po ON po.booking_id = o.booking_id
            WHERE po.payout_id = ?
        """, payoutId);

        // 3) 刪掉這張撥款單對應的 payout_orders（保留 host_payouts 做為稽核痕跡）
        jdbc.update("DELETE FROM payout_orders WHERE payout_id = ?", payoutId);

        // 4) 標記 host_payouts 為 cancelled（可視需要記 updated_by / 取消原因）
        jdbc.update("""
            UPDATE host_payouts
            SET status = 'cancelled',
                updated_at = SYSUTCDATETIME()
            WHERE payout_id = ?
        """, payoutId);
    }
}