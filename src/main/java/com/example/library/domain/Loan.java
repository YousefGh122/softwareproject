package com.example.library.domain;

import java.time.LocalDate;

public class Loan {
    private Integer loanId;
    private Integer userId;
    private Integer itemId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    
    // Constructors
    public Loan() {
    }
    
    public Loan(Integer loanId, Integer userId, Integer itemId, LocalDate loanDate, 
                LocalDate dueDate, LocalDate returnDate, String status) {
        this.loanId = loanId;
        this.userId = userId;
        this.itemId = itemId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }
    
    // Getters and Setters
    public Integer getLoanId() {
        return loanId;
    }
    
    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getItemId() {
        return itemId;
    }
    
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
    
    public LocalDate getLoanDate() {
        return loanDate;
    }
    
    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                '}';
    }
}
