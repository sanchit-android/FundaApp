package com.sanchit.funda.content.rest;

public abstract class AbstractRestEnricher<I, O> {

    public abstract O enrich(I input);

}
