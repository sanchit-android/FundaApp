package com.sanchit.funda.async.event;

public interface OnEnrichmentCompleted<T> {

    void updateView(T data);

}
