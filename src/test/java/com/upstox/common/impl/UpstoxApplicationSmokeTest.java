package com.upstox.common.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.dto.BarChartData;
import com.upstox.dto.Trade;
import com.upstox.workers.ClientSubscriptionWorker;
import com.upstox.workers.FiniteStateMachineConsumer;
import com.upstox.workers.TradeDataProducer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UpstoxApplicationSmokeTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testTradingData() throws InterruptedException, IOException {
        Queue<Trade> tradeMessageQueue = new ConcurrentLinkedQueue<>();
        TradeDataProducer tradeDataProducer = new TradeDataProducer("trades-less-data.json", tradeMessageQueue);
        FiniteStateMachineConsumer finiteStateMachineConsumer = new FiniteStateMachineConsumer(tradeMessageQueue);
        new Thread(tradeDataProducer).start();
        new Thread(finiteStateMachineConsumer).start();

        ClientSubscriptionWorker clientSubscriptionWorker = new ClientSubscriptionWorker(finiteStateMachineConsumer);
        Thread clientSubscriberWorkerThread = new Thread(clientSubscriptionWorker);
        clientSubscriberWorkerThread.start();

        Thread.sleep(20000);

        ConcurrentHashMap<String, List<BarChartData>> mapOfStockNameWithBarCharts = clientSubscriptionWorker.getMapOfStockNameWithBarCharts();
        String closingXZECXXBTData = "{\"volume\":0.1,\"event\":\"ohlc_notify\",\"o\":0.01947,\"h\":0.01947,\"l\":0.01947,\"c\":0.01947,\"symbol\":\"XZECXXBT\",\"bar_num\":1}";
        Assert.assertEquals(11, mapOfStockNameWithBarCharts.size());
        Assert.assertEquals(closingXZECXXBTData, mapper.writeValueAsString(mapOfStockNameWithBarCharts.get("XZECXXBT").stream().filter(x -> null != x.getClosingPrice()).findFirst().get()));
    }
}
