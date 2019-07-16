package com.author.model;

public class Book {
    private String BookTitle, SHortDescription, dateCreated, URL;
    private Long price;
    public Book() {
    }

    public Book(String BookTitle, String SHortDescription, String dateCreated, Long price, String URL) {
        this.BookTitle = BookTitle;
        this.SHortDescription = SHortDescription;
        this.dateCreated = dateCreated;
        this.price = price;
        this.URL = URL;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String bookName) {
        this.BookTitle = BookTitle;


    }

    public String getShortDescription() {
        return SHortDescription;
    }

    public void setShortDescription(String shortdescription) {

        this.SHortDescription = SHortDescription;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
    public Long getPrice(){
        return price;

    }
    public void setPrice(Long price){
        this.price = price;


    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated (String dateCreated) {
        this.dateCreated = dateCreated;
    }
}


