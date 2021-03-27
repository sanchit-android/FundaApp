package com.sanchit.funda.model;

import com.sanchit.funda.model.MutualFund;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MFRankModel implements Serializable {

    private MutualFund mutualFund;
    private Map<String, Integer> rankMap = new HashMap<>();

    public void setRank(String duration, Integer _rank) {
        rankMap.put(duration, _rank);
    }

    public Integer getRank(String duration) {
        return rankMap.get(duration);
    }

    public MutualFund getMutualFund() {
        return mutualFund;
    }

    public void setMutualFund(MutualFund mutualFund) {
        this.mutualFund = mutualFund;
    }
}
