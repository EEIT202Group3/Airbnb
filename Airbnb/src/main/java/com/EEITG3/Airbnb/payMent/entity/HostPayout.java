package com.EEITG3.Airbnb.payMent.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "host_payouts", indexes = {
		@Index(name = "idx_host_payouts_host_month", columnList = "hostId, payoutMonth")
})
public class HostPayout {

	    @Id
	    @Column(name = "payout_id", nullable = false)
	    private String payoutId;

	    @Column(name = "host_id", nullable = false)
	    private String hostId;  

	    @Column(name = "payout_month", nullable = false, length = 7)
	    private String payoutMonth; 

	    @Column(name = "total_earnings", nullable = false, precision = 10, scale = 0)
	    private BigDecimal totalEarnings;

	    @Column(name = "total_platform_fee", nullable = false, precision = 10, scale = 0)
	    private BigDecimal totalPlatformFee;

	    @Column(name = "total_net_payout", nullable = false, precision = 10, scale = 0)
	    private BigDecimal totalNetPayout;

	    @Column(name = "status", nullable = false, length = 20)
	    private String status = "pending";

	    @Column(name = "payout_date")
	    private LocalDateTime payoutDate;

	    @Column(name = "created_at", nullable = false)
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at")
	    private LocalDateTime updatedAt;

	    // 與 payout_orders 的一對多關聯
	    @OneToMany(mappedBy = "hostPayout", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<PayoutOrder> payoutOrders;

		public String getPayoutId() {
			return payoutId;
		}

		public void setPayoutId(String payoutId) {
			this.payoutId = payoutId;
		}

		public String getHostId() {
			return hostId;
		}

		public void setHostId(String hostId) {
			this.hostId = hostId;
		}

		public String getPayoutMonth() {
			return payoutMonth;
		}

		public void setPayoutMonth(String payoutMonth) {
			this.payoutMonth = payoutMonth;
		}

		public BigDecimal getTotalEarnings() {
			return totalEarnings;
		}

		public void setTotalEarnings(BigDecimal totalEarnings) {
			this.totalEarnings = totalEarnings;
		}

		public BigDecimal getTotalPlatformFee() {
			return totalPlatformFee;
		}

		public void setTotalPlatformFee(BigDecimal totalPlatformFee) {
			this.totalPlatformFee = totalPlatformFee;
		}

		public BigDecimal getTotalNetPayout() {
			return totalNetPayout;
		}

		public void setTotalNetPayout(BigDecimal totalNetPayout) {
			this.totalNetPayout = totalNetPayout;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public LocalDateTime getPayoutDate() {
			return payoutDate;
		}

		public void setPayoutDate(LocalDateTime payoutDate) {
			this.payoutDate = payoutDate;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}

		public List<PayoutOrder> getPayoutOrders() {
			return payoutOrders;
		}

		public void setPayoutOrders(List<PayoutOrder> payoutOrders) {
			this.payoutOrders = payoutOrders;
		}
	    
}
