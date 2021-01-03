package com.upstox.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.dto.BarChartData;
import com.upstox.dto.SubscriptionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class is worker 3 which Maintains client list,
 * <p>
 * and publishes (transmits) the BAR OHLC data as computed in real time.
 */
public class ClientSubscriptionWorker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSubscriptionWorker.class);
    public static final List<SubscriptionDetails> subscriptionDetails = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final FiniteStateMachineConsumer finiteStateMachineConsumer;
    private final ConcurrentHashMap<String, List<BarChartData>> mapOfStockNameWithBarCharts = new ConcurrentHashMap<>();

    static {
        //Assuming below hard coded Subscribers
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "XZECXXBT", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "XXBTZUSD", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "XETHZUSD", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "XXLMXXBT", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "ADAEUR", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "GNOXBT", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "ADAXBT", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "XXRPXXBT", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "XXMRXXBT", 15));
        subscriptionDetails.add(new SubscriptionDetails("subscribe", "XETHXXBT", 15));
    }

    public ClientSubscriptionWorker(FiniteStateMachineConsumer finiteStateMachineConsumer) {
        this.finiteStateMachineConsumer = finiteStateMachineConsumer;
    }

    @Override
    public void run() {
        Set<String> subscribers = subscriptionDetails.stream().map(SubscriptionDetails::getSymbol).collect(Collectors.toSet());
        while (true) {
            BarChartData barChartData = null;
            try {
                barChartData = finiteStateMachineConsumer.getBarChartMessage();
            } catch (InterruptedException interruptedException) {
                LOGGER.error("Interrupted Exception {}", interruptedException);
            }
            if (null == barChartData) {
                return;
            }
            String stockName = barChartData.getStockName();
            mapOfStockNameWithBarCharts.put(stockName, getListOfBarChartData(barChartData));
            try {
                if (subscribers.contains(stockName)) {
                    //This message should be published to clients
                    LOGGER.info("Stock {} Subscriber OHLC output = {}", stockName, mapper.writeValueAsString(barChartData));
                } else {
                    LOGGER.info("Not to Publish as Stock {} it not subscribed OHLC output = {}", stockName, mapper.writeValueAsString(barChartData));
                }
            } catch (IOException ex) {
                LOGGER.error("Unable to parse Bar Chart Data for stock {} {}", stockName, ex);
            }
        }
    }

    private List<BarChartData> getListOfBarChartData(BarChartData barChartData) {
        List<BarChartData> existingList = mapOfStockNameWithBarCharts.get(barChartData.getStockName());
        if (null == existingList || existingList.isEmpty()) {
            existingList = new ArrayList<>();
        }
        existingList.add(barChartData);
        return existingList;
    }

    public ConcurrentHashMap<String, List<BarChartData>> getMapOfStockNameWithBarCharts() {
        return mapOfStockNameWithBarCharts;
    }
}
