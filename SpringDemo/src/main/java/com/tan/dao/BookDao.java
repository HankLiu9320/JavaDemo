package com.tan.dao;

import org.springframework.stereotype.Component;

import com.tan.model.Book;

@Component
public class BookDao {
    public BookDao() {
        super();
        System.err.println("BookDao 初始化！");
    }
    public void add(Book book) {
        System.out.print("Add");
    }

    public void update(Book book) {
        System.out.print("Update");
    }
}
