package com.sanchit.funda.async.task;

public class TaskChainManager implements Chainable {

    ChainedTask rootTask;

    private TaskChainManager() {
    }

    public TaskChainManager newInstance() {
        TaskChainManager m = new TaskChainManager();
        return m;
    }

    @Override
    public ChainedTask chain(ChainedTask task) {
        rootTask = task;
        return rootTask;
    }
}
