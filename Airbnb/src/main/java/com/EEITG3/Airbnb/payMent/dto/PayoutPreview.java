package com.EEITG3.Airbnb.payMent.dto;

import java.util.List;

public class PayoutPreview {
	private String payoutMonth;
	private List<PayoutRow> rows;

	public PayoutPreview(String payoutMonth, List<PayoutRow> rows) {
		this.payoutMonth = payoutMonth;
		this.rows = rows;
	}

	public String getPayoutMonth() {
		return payoutMonth;
	}

	public void setPayoutMonth(String payoutMonth) {
		this.payoutMonth = payoutMonth;
	}

	public List<PayoutRow> getRows() {
		return rows;
	}

	public void setRows(List<PayoutRow> rows) {
		this.rows = rows;
	}
}