package com.upstox;

import com.upstox.dto.Trade;
import com.upstox.workers.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UpstoxApplicationRunner {
    public static void main(String[] args) {
        Queue<Trade> tradeMessageQueue = new ConcurrentLinkedQueue<>();
        TradeDataProducer tradeDataProducer = new TradeDataProducer("trades.json", tradeMessageQueue);
        FiniteStateMachineConsumer finiteStateMachineConsumer = new FiniteStateMachineConsumer(tradeMessageQueue);
        new Thread(tradeDataProducer).start();
        new Thread(finiteStateMachineConsumer).start();

        ClientSubscriptionWorker clientSubscriptionWorker = new ClientSubscriptionWorker(finiteStateMachineConsumer);
        new Thread(clientSubscriptionWorker).start();
    }
}
