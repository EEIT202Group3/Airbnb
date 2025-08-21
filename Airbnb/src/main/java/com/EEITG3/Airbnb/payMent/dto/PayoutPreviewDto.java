package com.EEITG3.Airbnb.payMent.dto;

import java.util.List;

public class PayoutPreviewDto {
	private String payoutMonth;
	private List<PayoutRowDto> rows;

	public PayoutPreviewDto(String payoutMonth, List<PayoutRowDto> rows) {
		this.payoutMonth = payoutMonth;
		this.rows = rows;
	}

	public String getPayoutMonth() {
		return payoutMonth;
	}

	public void setPayoutMonth(String payoutMonth) {
		this.payoutMonth = payoutMonth;
	}

	public List<PayoutRowDto> getRows() {
		return rows;
	}

	public void setRows(List<PayoutRowDto> rows) {
		this.rows = rows;
	}
}