package com.upstox.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.Transient;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BarChartData {
    @JsonProperty("o")
    private BigDecimal openingPrice;
    @JsonProperty("h")
    private BigDecimal highPrice;
    @JsonProperty("l")
    private BigDecimal lowPrice;
    @JsonProperty("c")
    private BigDecimal closingPrice;
    private double volume;
    private String event;
    @JsonProperty("symbol")
    private String stockName;
    @JsonProperty("bar_num")
    private int barNumber;

    public BigDecimal getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(BigDecimal openingPrice) {
        this.openingPrice = openingPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(BigDecimal closingPrice) {
        this.closingPrice = closingPrice;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getBarNumber() {
        return barNumber;
    }

    public void setBarNumber(int barNumber) {
        this.barNumber = barNumber;
    }
}
