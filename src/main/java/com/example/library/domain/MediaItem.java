package com.example.library.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MediaItem {
    private Integer itemId;
    private String title;
    private String author;
    private String type;
    private String isbn;
    private LocalDate publicationDate;
    private String publisher;
    private Integer totalCopies;
    private Integer availableCopies;
    private BigDecimal lateFeesPerDay;
    
    // Constructors
    public MediaItem() {
    }
    
    public MediaItem(Integer itemId, String title, String author, String type, String isbn, 
                     LocalDate publicationDate, String publisher, Integer totalCopies, 
                     Integer availableCopies, BigDecimal lateFeesPerDay) {
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.type = type;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.publisher = publisher;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.lateFeesPerDay = lateFeesPerDay;
    }
    
    // Getters and Setters
    public Integer getItemId() {
        return itemId;
    }
    
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public Integer getTotalCopies() {
        return totalCopies;
    }
    
    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }
    
    public Integer getAvailableCopies() {
        return availableCopies;
    }
    
    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
    
    public BigDecimal getLateFeesPerDay() {
        return lateFeesPerDay;
    }
    
    public void setLateFeesPerDay(BigDecimal lateFeesPerDay) {
        this.lateFeesPerDay = lateFeesPerDay;
    }
    
    @Override
    public String toString() {
        return "MediaItem{" +
                "itemId=" + itemId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publicationDate=" + publicationDate +
                ", publisher='" + publisher + '\'' +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                ", lateFeesPerDay=" + lateFeesPerDay +
                '}';
    }
}
