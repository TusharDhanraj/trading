package com.upstox.translator;

import com.upstox.dto.BarChartData;
import com.upstox.dto.Trade;

import java.math.BigDecimal;

public class BarChartDataTranslator {
    public BarChartData translateToDefaultBarChartData(Trade trade, int barNumber) {
        if (null == trade) {
            return null;
        }

        BarChartData barChartData = new BarChartData();
        barChartData.setBarNumber(barNumber);
        barChartData.setEvent("ohlc_notify");
        barChartData.setStockName(trade.getStockName());
        return barChartData;
    }

    public BarChartData translateToBarChartData(Trade trade, int barNumber, BigDecimal openingPrice
            , BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closingPrice, double totalQuantity) {
        if (null == trade) {
            return null;
        }

        BarChartData barChartData = new BarChartData();
        barChartData.setBarNumber(barNumber);
        barChartData.setOpeningPrice(openingPrice);
        barChartData.setHighPrice(highPrice);
        barChartData.setLowPrice(lowPrice);
        barChartData.setClosingPrice(closingPrice);
        barChartData.setEvent("ohlc_notify");
        barChartData.setStockName(trade.getStockName());
        barChartData.setVolume(totalQuantity);
        return barChartData;
    }
}
