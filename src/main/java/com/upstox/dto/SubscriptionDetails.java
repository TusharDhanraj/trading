package com.upstox.dto;

public class SubscriptionDetails {
    private String event;
    private String symbol;
    private int interval;

    public SubscriptionDetails(String event, String symbol, int interval) {
        this.event = event;
        this.symbol = symbol;
        this.interval = interval;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
