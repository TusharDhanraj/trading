package com.upstox.workers;

import com.upstox.common.FileReader;
import com.upstox.common.impl.FileReaderJsonOneLinerImpl;
import com.upstox.dto.Trade;
import io.prometheus.client.Histogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Queue;
import java.util.stream.Stream;

/**
 * This class is worker 1 which Reads the Trades data input (line by line from JSON), and sends
 * <p>
 * the packet to the FSM (Finite-State-Machine) thread
 */
public class TradeDataProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeDataProducer.class);
    private final Queue<Trade> tradeMessageQueue;
    private final String fileName;
    private static final Histogram histogram = getHistogram();

    public TradeDataProducer(String fileName,Queue<Trade> tradeMessageQueue) {
        this.fileName=fileName;
        this.tradeMessageQueue = tradeMessageQueue;
    }

    @Override
    public void run() {
        LOGGER.info("Trade data file reading and publishing trade to queue started");
        Histogram.Timer requestTimer = histogram.labels("Read file and Publish data to queue").startTimer();
        FileReader fileReader =
                new FileReaderJsonOneLinerImpl(fileName,Trade.class);
        //TODO: In future this can be run infinite using while(true) if the above file data is continuously growing.
        // Right now assuming this trade data is static
        try (Stream<Trade> tradeStream = fileReader.readAll()) {
            tradeStream.forEach(this::publishMessage);
        }
        LOGGER.info("Trade data file reading and publishing trade to queue completed. Total time took = {} Second", requestTimer.observeDuration());
    }

    private static Histogram getHistogram() {
        return Histogram.build().labelNames("TradeDataProducer").name("request_latency_in_second")
                .help("Request latency in second").register();
    }

    private void publishMessage(Trade trade) {
        tradeMessageQueue.add(trade);
    }
}
