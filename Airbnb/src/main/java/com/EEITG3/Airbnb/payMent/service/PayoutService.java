package com.EEITG3.Airbnb.payMent.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.payMent.dto.HostOrderAggDto;
import com.EEITG3.Airbnb.payMent.entity.HostPayout;
import com.EEITG3.Airbnb.payMent.entity.PayoutOrder;
import com.EEITG3.Airbnb.payMent.repository.HostPayoutRepository;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.payMent.repository.PayoutOrderRepository;

import jakarta.transaction.Transactional;

@Service
public class PayoutService {

    private final OrderRepository orderRepository;
    private final HostPayoutRepository hostPayoutRepository;
    private final PayoutOrderRepository payoutOrderRepository;

    public PayoutService(OrderRepository orderRepository,
                         HostPayoutRepository hostPayoutRepository,
                         PayoutOrderRepository payoutOrderRepository) {
        this.orderRepository = orderRepository;
        this.hostPayoutRepository = hostPayoutRepository;
        this.payoutOrderRepository = payoutOrderRepository;
    }

    // 每月5號要結算「上個月」付款完成的訂單
    @Transactional
    public void generateMonthlyPayouts(String month) {
        ZoneId tz = ZoneId.of("Asia/Taipei");
        LocalDate first = LocalDate.parse(month + "-01");
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end   = first.plusMonths(1).atStartOfDay();
        String payoutMonth  = month;

        // 1) 撈上月所有已付款訂單 (DTO)
        List<HostOrderAggDto> rows = orderRepository.findPaidOrdersForPayout(start, end);

        // 2) 依房東分組
        Map<String, List<HostOrderAggDto>> byHost = rows.stream()
                .collect(Collectors.groupingBy(HostOrderAggDto::getHostId));

        // 3) 逐一房東建立 HostPayout + PayoutOrder
        byHost.forEach((hostId, items) -> {
            // 同一房東同一月份只建一次
            if (hostPayoutRepository.existsByHostIdAndPayoutMonth(hostId, payoutMonth)) return;

            BigDecimal totalGross = BigDecimal.ZERO;
            BigDecimal totalFee   = BigDecimal.ZERO;
            BigDecimal totalNet   = BigDecimal.ZERO;

            HostPayout hp = new HostPayout();
            hp.setPayoutId(UUID.randomUUID().toString());
            hp.setHostId(hostId);
            hp.setPayoutMonth(payoutMonth);
            hp.setStatus("pending");
            hp.setCreatedAt(LocalDateTime.now(tz));
            hp = hostPayoutRepository.save(hp);

            for (HostOrderAggDto i : items) {
                BigDecimal gross = i.getTotalAmount();
                BigDecimal fee   = calcPlatformFee(gross);
                BigDecimal net   = gross.subtract(fee);

                PayoutOrder po = new PayoutOrder();
                po.setPayoutOrderId(UUID.randomUUID().toString());
                po.setHostPayout(hp);
                po.setBookingId(i.getBookingId());
                po.setListId(String.valueOf(i.getListId()));
                po.setGrossAmount(gross);
                po.setPlatformFee(fee);
                po.setNetAmount(net);
                po.setCreatedAt(LocalDateTime.now(tz));
                payoutOrderRepository.save(po);

                totalGross = totalGross.add(gross);
                totalFee   = totalFee.add(fee);
                totalNet   = totalNet.add(net);
            }

            hp.setTotalEarnings(totalGross);
            hp.setTotalPlatformFee(totalFee);
            hp.setTotalNetPayout(totalNet);
            hp.setUpdatedAt(LocalDateTime.now(tz));
            hostPayoutRepository.save(hp);
        });
    }

    private BigDecimal calcPlatformFee(BigDecimal gross) {
        // 例：15% 抽成，保留2位小數
        return gross.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);
    }
}
