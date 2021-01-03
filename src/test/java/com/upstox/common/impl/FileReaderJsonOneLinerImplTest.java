package com.upstox.common.impl;

import com.upstox.dto.Trade;
import com.upstox.exception.UpstoxException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReaderJsonOneLinerImplTest extends TestCase {

    private FileReaderJsonOneLinerImpl fileReaderJsonOneLiner = new FileReaderJsonOneLinerImpl("trades.json", Trade.class);

    @Test
    public void testReadAll() {
        Stream<Trade> tradeStream = fileReaderJsonOneLiner.readAll();
        List<Trade> tradeList = tradeStream.collect(Collectors.toList());

        Assert.assertEquals(178014, tradeList.size());
        Assert.assertEquals(40529, tradeList.stream().filter(x -> "XETHZUSD".equalsIgnoreCase(x.getStockName())).count());
        Assert.assertEquals(0, tradeList.stream().filter(x -> "HILTI".equalsIgnoreCase(x.getStockName())).count());
    }

    @Test
    public void testReadAll_FileNotFound() {
        fileReaderJsonOneLiner = new FileReaderJsonOneLinerImpl("trades2.json", Trade.class);
        Assertions.assertThrows(UpstoxException.class, () -> fileReaderJsonOneLiner.readAll());
    }

    @Test
    public void testReadAll_InvalidJson() {
        fileReaderJsonOneLiner = new FileReaderJsonOneLinerImpl("trades-less-data-invalid-data.json", Trade.class);
        Stream<Trade> tradeStream = fileReaderJsonOneLiner.readAll();
        Assertions.assertThrows(UpstoxException.class, () -> tradeStream.collect(Collectors.toList()));
    }
}