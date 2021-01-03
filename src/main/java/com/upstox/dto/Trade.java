package com.upstox.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    @JsonProperty("sym")
    private String stockName;
    @JsonProperty("P")
    private BigDecimal price;
    @JsonProperty("Q")
    private double quantity;
    private String side;
    @JsonProperty("TS2")
    private BigDecimal timeStamp;

    public Trade() {
    }

    public Trade(String stockName) {
        this.stockName = stockName;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public BigDecimal getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(BigDecimal timeStamp) {
        this.timeStamp = timeStamp;
    }
}
