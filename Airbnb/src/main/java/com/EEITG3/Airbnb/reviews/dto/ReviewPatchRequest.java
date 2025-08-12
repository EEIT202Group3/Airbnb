package com.EEITG3.Airbnb.reviews.dto;

public class ReviewPatchRequest {
	
	
	private Integer cleanScore;
    private	Integer commScore;
    private Integer valueScore;
    private String cusComm;
    
    
	public Integer getCleanScore() {
		return cleanScore;
	}
	public void setCleanScore(int cleanScore) {
		this.cleanScore = cleanScore;
	}
	public Integer getCommScore() {
		return commScore;
	}
	public void setCommScore(int commScore) {
		this.commScore = commScore;
	}
	public Integer getValueScore() {
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
    
    

}
