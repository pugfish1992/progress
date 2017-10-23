package com.pugfish1992.progress.utils;

/**
 * Created by daichi on 10/23/17.
 */

public class CheapIdGenerator {

    private int mCurrentId;

    public CheapIdGenerator() {
        this(0);
    }

    public CheapIdGenerator(int beginId) {
        mCurrentId = beginId - 1;
    }

    public int generate() {
        return ++mCurrentId;
    }
}
