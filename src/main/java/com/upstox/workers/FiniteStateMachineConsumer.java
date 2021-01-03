package com.upstox.workers;

import com.upstox.dto.BarChartData;
import com.upstox.dto.Trade;
import com.upstox.translator.BarChartDataTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * This class is a worker 2 (FSM) computes OHLC packets based on 15 seconds (interval)
 *
 * and constructs 'BAR' chart data, based on timestamp TS2.
 */
public class FiniteStateMachineConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FiniteStateMachineConsumer.class);
    private final Queue<Trade> tradeMessageQueue;
    private final ConcurrentHashMap<String, BarChartData> mapOfStockNameWithLastBarChart = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Trade>> mapOfStockNameWithTrades = new ConcurrentHashMap<>();
    private static int barNumber = 1;
    private static final int MAXIMUM_INTERVAL_IN_MILLISECOND = 15000;
    private static final int MAX = 10;
    private final Vector<BarChartData> barChartMessageQueue = new Vector<>();

    public FiniteStateMachineConsumer(Queue<Trade> tradeMessageQueue) {
        this.tradeMessageQueue = tradeMessageQueue;
    }

    @Override
    public void run() {
        long currentTimeMillis = System.currentTimeMillis();
        long endTime = currentTimeMillis + MAXIMUM_INTERVAL_IN_MILLISECOND;
        while (true) {
            Trade trade;
            if (System.currentTimeMillis() < endTime) {
                trade = tradeMessageQueue.poll();
                processData(trade);
            } else {
                barNumber++;
                BarChartDataTranslator barChartDataTranslator = new BarChartDataTranslator();
                mapOfStockNameWithLastBarChart.forEach((k, v) ->
                {
                    try {
                        //Setting closing price only in case Opening price is available i.e. if not a default BarChart
                        if (null != v.getOpeningPrice()) {
                            setClosingPrice(k, v);
                            publishBarChartData(v);
                            LOGGER.info("Closing bar chart data for stock {} Published", k);
                        }
                        //Updating to default not a Trading data
                        BarChartData defaultBarChartData = barChartDataTranslator.translateToDefaultBarChartData(new Trade(k), barNumber);
                        mapOfStockNameWithLastBarChart.put(k,
                                defaultBarChartData);
                        publishBarChartData(defaultBarChartData);
                        LOGGER.info("Default bar chart data for stock {} Published", k);
                    } catch (InterruptedException ex) {
                        LOGGER.error("Interrupted Exception for stock {}", k, ex);
                    }
                });

                //Reset 15 second checks
                currentTimeMillis = System.currentTimeMillis();
                endTime = currentTimeMillis + MAXIMUM_INTERVAL_IN_MILLISECOND;
                mapOfStockNameWithTrades.clear();
            }
        }
    }

    private void setClosingPrice(String k, BarChartData v) {
        List<Trade> trades = mapOfStockNameWithTrades.get(k);
        if (null == trades || trades.isEmpty()) {
            return;
        }
        Trade lastTrade = trades.stream().reduce((first, last) -> last).get();
        v.setClosingPrice(lastTrade.getPrice());
    }

    private void processData(Trade trade) {
        if (null == trade) {
            return;
        }
        BarChartData barChartData;
        try {
            BarChartDataTranslator barChartDataTranslator = new BarChartDataTranslator();
            String stockName = trade.getStockName();
            List<Trade> trades = mapOfStockNameWithTrades.get(stockName);
            setMapOfSymbolNameWithTrades(trade, stockName, trades);
            BarChartData lastBarChartData = mapOfStockNameWithLastBarChart.get(stockName);
            BigDecimal currentTradePrice = trade.getPrice();
            if (null == lastBarChartData) {
                barChartData = barChartDataTranslator.translateToBarChartData(trade,
                        barNumber, currentTradePrice, currentTradePrice, currentTradePrice
                        , BigDecimal.ZERO, trade.getQuantity());
            } else {
                barChartData = barChartDataTranslator.translateToBarChartData(trade,
                        lastBarChartData.getBarNumber(), lastBarChartData.getOpeningPrice(), transformToHighPrice(trades), transformToLowPrice(trades)
                        , BigDecimal.ZERO, transformToVolume(trades));
            }

            publishBarChartData(barChartData);
            //Put in map for future use mainly 15 sec interval
            mapOfStockNameWithLastBarChart.put(stockName, barChartData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BigDecimal transformToHighPrice(List<Trade> trades) {
        if (null == trades || trades.isEmpty()) {
            return null;
        }
        Optional<BigDecimal> max = trades.stream().filter(x -> null != x.getPrice()).map(Trade::getPrice).max(BigDecimal::compareTo);
        return max.orElse(null);
    }

    private BigDecimal transformToLowPrice(List<Trade> trades) {
        if (null == trades || trades.isEmpty()) {
            return null;
        }
        Optional<BigDecimal> min = trades.stream().filter(x -> null != x.getPrice()).map(Trade::getPrice).min(BigDecimal::compareTo);
        return min.orElse(null);
    }

    private double transformToVolume(List<Trade> trades) {
        if (null == trades || trades.isEmpty()) {
            return 0;
        }
        return trades.stream().map(Trade::getQuantity).mapToDouble(Double::doubleValue).sum();
    }

    private void setMapOfSymbolNameWithTrades(Trade trade, String stockName, List<Trade> trades) {
        if (null == trades || trades.isEmpty()) {
            List<Trade> newTradeList = new ArrayList<>();
            newTradeList.add(trade);
            mapOfStockNameWithTrades.put(stockName, newTradeList);
        } else {
            trades.add(trade);
            mapOfStockNameWithTrades.put(stockName, trades);
        }
    }

    private synchronized void publishBarChartData(BarChartData barChartData) throws InterruptedException {
        if (barChartMessageQueue.size() == MAX) {
            wait();
        }
        barChartMessageQueue.addElement(barChartData);
        notify();
    }

    public synchronized BarChartData getBarChartMessage() throws InterruptedException {
        notify();
        while (0 == barChartMessageQueue.size()) {
            wait();
        }

        BarChartData barChartData = barChartMessageQueue.firstElement();
        // remove the message from the queue
        barChartMessageQueue.removeElement(barChartData);
        return barChartData;
    }
}
