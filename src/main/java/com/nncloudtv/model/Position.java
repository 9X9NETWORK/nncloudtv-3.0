package com.nncloudtv.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Temporarily table. 
 * It's only used for quick query for user subscription available position and not really used in code anywhere.  
 */
@PersistenceCapable(table="position", detachable="true")
public class Position {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private short seq; //has 72 records, from 1-72

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }
        
}
