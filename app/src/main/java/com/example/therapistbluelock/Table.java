package com.example.therapistbluelock;

import java.util.List;

public class Table {
    private String name;
    private List<TableDetail> details;
    private List<String> headers;

    public Table(String name, List<TableDetail> details, List<String> headers) {
        this.name = name;
        this.details = details;
        this.headers = headers;
    }

    public String getName() {
        return name;
    }

    public List<TableDetail> getDetails() {
        return details;
    }

    public List<String> getHeaders() {
        return headers;
    }
}

