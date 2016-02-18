package com.nigelcardozo.screenscraper;

//This class supports the EventBus library. Its purpose is to allow the model to notify the
//view when there is new data to be retrieved and displayed.

public class MessageEvent {

    public final String message;

    public MessageEvent(String message) {
        this.message = message;
    }
}