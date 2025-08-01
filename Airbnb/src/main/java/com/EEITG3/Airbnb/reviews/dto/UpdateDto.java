package com.EEITG3.Airbnb.reviews.dto;

public class UpdateDto {
	
	private int cleanScore;
	private int commScore;
	private int valueScore;
	private String cusComm;
	private String hostComm;
	
	public int getCleanScore() {
		return cleanScore;
	}
	public void setCleanScore(int cleanScore) {
		this.cleanScore = cleanScore;
	}
	public int getCommScore() {
		return commScore;
	}
	public void setCommScore(int commScore) {
		this.commScore = commScore;
	}
	public int getValueScore() {
		return valueScore;
	}
	public void setValueScore(int valueScore) {
		this.valueScore = valueScore;
	}
	public String getCusComm() {
		return cusComm;
	}
	public void setCusComm(String cusComm) {
		this.cusComm = cusComm;
	}
	public String getHostComm() {
		return hostComm;
	}
	public void setHostComm(String hostComm) {
		this.hostComm = hostComm;
	}
	
}
