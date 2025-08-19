package com.EEITG3.Airbnb.payMent.dto;

public class PaymentRequestDto {
    private String orderNo;
    private Integer amount;
    private String description;
    private String email;
    public PaymentRequestDto() {}
    
    public PaymentRequestDto(String orderNo, Integer amount, String description, String email) {
        this.orderNo = orderNo;
        this.amount = amount;
        this.description = description;
        this.email = email;
    }

    public String getOrderNo() { 
        return orderNo; 
    }
    
    public void setOrderNo(String orderNo) { 
        this.orderNo = orderNo; 
    }
    
    public Integer getAmount() { 
        return amount; 
    }
    
    public void setAmount(Integer amount) { 
        this.amount = amount; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    @Override
    public String toString() {
        return "PaymentRequestDto{" +
                "orderNo='" + orderNo + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}