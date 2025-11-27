package com.example.library.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Fine {
    private Integer fineId;
    private Integer loanId;
    private BigDecimal amount;
    private LocalDate issuedDate;
    private String status;
    private LocalDate paidDate;
    
    // Constructors
    public Fine() {
    }
    
    public Fine(Integer fineId, Integer loanId, BigDecimal amount, LocalDate issuedDate, 
                String status, LocalDate paidDate) {
        this.fineId = fineId;
        this.loanId = loanId;
        this.amount = amount;
        this.issuedDate = issuedDate;
        this.status = status;
        this.paidDate = paidDate;
    }
    
    // Getters and Setters
    public Integer getFineId() {
        return fineId;
    }
    
    public void setFineId(Integer fineId) {
        this.fineId = fineId;
    }
    
    public Integer getLoanId() {
        return loanId;
    }
    
    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getIssuedDate() {
        return issuedDate;
    }
    
    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDate getPaidDate() {
        return paidDate;
    }
    
    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
    
    @Override
    public String toString() {
        return "Fine{" +
                "fineId=" + fineId +
                ", loanId=" + loanId +
                ", amount=" + amount +
                ", issuedDate=" + issuedDate +
                ", status='" + status + '\'' +
                ", paidDate=" + paidDate +
                '}';
    }
}
