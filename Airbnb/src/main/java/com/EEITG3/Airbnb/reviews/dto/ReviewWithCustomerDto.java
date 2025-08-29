	package com.EEITG3.Airbnb.reviews.dto;


public class ReviewWithCustomerDto {
	private Integer reviewId;
	private Integer cleanScore;
    private	Integer commScore;
    private Integer valueScore;
    private String reviewDate;
    private String cusComm;
    private String hostComm;
    
    private String email;
    private String avatarURL;
    private Integer isVisible;
    
	public Integer getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(Integer isVisible) {
		this.isVisible = isVisible;
	}
	public Integer getReviewId() {
		return reviewId;
	}
	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}
	public Integer getCleanScore() {
		return cleanScore;
	}
	public void setCleanScore(Integer cleanScore) {
		this.cleanScore = cleanScore;
	}
	public Integer getCommScore() {
		return commScore;
	}
	public void setCommScore(Integer commScore) {
		this.commScore = commScore;
	}
	public Integer getValueScore() {
		return valueScore;
	}
	public void setValueScore(Integer valueScore) {
		this.valueScore = valueScore;
	}
	public String getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAvatarURL() {
		return avatarURL;
	}
	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}
	public ReviewWithCustomerDto(Integer cleanScore, Integer commScore, Integer valueScore, String reviewDate,
			String cusComm, String hostComm, String email, String avatarURL) {
		super();
		this.cleanScore = cleanScore;
		this.commScore = commScore;
		this.valueScore = valueScore;
		this.reviewDate = reviewDate;
		this.cusComm = cusComm;
		this.hostComm = hostComm;
		this.email = email;
		this.avatarURL = avatarURL;
	}
	public ReviewWithCustomerDto(Integer reviewId, Integer cleanScore, Integer commScore, Integer valueScore,
			String reviewDate, String cusComm, String hostComm, String email, String avatarURL) {
		super();
		this.reviewId = reviewId;
		this.cleanScore = cleanScore;
		this.commScore = commScore;
		this.valueScore = valueScore;
		this.reviewDate = reviewDate;
		this.cusComm = cusComm;
		this.hostComm = hostComm;
		this.email = email;
		this.avatarURL = avatarURL;
	}
	public ReviewWithCustomerDto(Integer reviewId, Integer cleanScore, Integer commScore, Integer valueScore,
			String reviewDate, String cusComm, String hostComm, String email, String avatarURL, Integer isVisible) {
		this.reviewId = reviewId;
		this.cleanScore = cleanScore;
		this.commScore = commScore;
		this.valueScore = valueScore;
		this.reviewDate = reviewDate;
		this.cusComm = cusComm;
		this.hostComm = hostComm;
		this.email = email;
		this.avatarURL = avatarURL;
		this.isVisible = isVisible;
	}
	
	

	

}
