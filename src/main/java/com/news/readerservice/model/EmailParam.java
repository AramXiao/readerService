package com.news.readerservice.model;

import java.util.HashMap;
import java.util.Map;

public class EmailParam {

    private Map<String, Object> emailData = new HashMap<>();


    public Map<String, Object> getEmailData() {
        return emailData;
    }

    public void setEmailData(Map<String, Object> emailData) {
        this.emailData = emailData;
    }
}
