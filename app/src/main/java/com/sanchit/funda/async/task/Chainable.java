package com.sanchit.funda.async.task;

public interface Chainable {

    public ChainedTask chain(ChainedTask task);
}
