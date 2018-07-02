package com.andrognito.patternlockdemo;

import com.andrognito.patternlockview.PatternLockView;

import java.util.ArrayList;
import java.util.List;

public class MySavePattern {

    private List<PatternLockView.Dot> olddots=new ArrayList<>();
    private List<PatternLockView.Dot> newdots=new ArrayList<>();

    public List<PatternLockView.Dot> getOlddots() {
        return olddots;
    }

    public void setOlddots(List<PatternLockView.Dot> olddots) {
        this.olddots.addAll(olddots);
    }

    public List<PatternLockView.Dot> getNewdots() {
        return newdots;
    }

    public void setNewdots(List<PatternLockView.Dot> newdots) {
        this.newdots.addAll(newdots);
    }
}
