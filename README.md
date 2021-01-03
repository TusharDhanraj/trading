#Prerequisite
    
    Java 8 +
    
    Gradle 6.7.1
    
    
#How to Run

    1. Download zip or checkout this project from git
    
    2. Jump to folder and run using below two options
        a. Can be run using => gradle run
        
        b. UpstoxApplicationRunner is a Main class, can be run using any IDE
        
        c. First clean build to create lib and then run second command
            gradle clean build
            java -jar build\libs\upstox-assignment-1.0-SNAPSHOT.jar


#Three workers create details are as follows

    1. TradeDataProducer -  Reads trade.json and publishes event data to Finite State Machine worker (FiniteStateMachineConsumer)
    
    2. FiniteStateMachineConsumer - Reads trade data, computes OHLC packets based on 15 seconds (interval)
       and constructs 'BAR' chart data, based on timestamp TS2. Published events to worker 3 (ClientSubscriptionWorker)
       
    3. ClientSubscriptionWorker - Maintains client list and publishes (transmits) the BAR OHLC data as computed in real time
    

###### Non blocking queue ConcurrentLinkedQueue is being used as I wanted thread safe queueing and lock free for faster processing.


## Output OHLC

 ###### Closing bar chart data for stock XETHZUSD Published
        INFO  ClientSubscriptionWorker - Stock XETHZUSD Subscriber OHLC output =
        {"volume":197.43950999999998,"event":"ohlc_notify","o":226.85,"h":226.85,"l":226.26,"c":226.26,"symbol":"XETHZUSD","bar_num":1}

 ###### Not to Publish as Stock Hilti it not subscribed OHLC output =
        {"volume":6.0,"event":"ohlc_notify","o":0.00001272,"h":0.00001272,"l":0.00001272,"c":0.00001272,"symbol":"Hilti","bar_num":1}

 ###### Default in case no trading data available OHLC output =
        {"volume":0.0,"event":"ohlc_notify","symbol":"XETHZUSD","bar_num":3}

 ###### Performance statistics (Sample done for 1)
        TradeDataProducer - Trade data file reading and publishing trade to queue completed. Total time took = 0.342181 Second

 ###### Additionally logs are also added to log file name upstox-service.log, which is generate in project folder