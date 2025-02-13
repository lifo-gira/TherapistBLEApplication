package com.example.therapistbluelock;

import java.util.Map;

class TableDetail {
    private int id;
    private Map<String, String> params;

    public TableDetail(int id, Map<String, String> params) {
        this.id = id;
        this.params = params;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
