package com.nncloudtv.service;

import java.util.Comparator;

import com.nncloudtv.model.NnProgram;

public class NnProgramSeqComparator implements Comparator<NnProgram> {
    
    public int compare(NnProgram program1, NnProgram program2) {
        
        int seq1 = program1.getSeqInt();
        int seq2 = program2.getSeqInt();
        int subSeq1 = program1.getSubSeqInt();
        int subSeq2 = program2.getSubSeqInt();
        
        return (seq1 == seq2) ? (subSeq1 - subSeq2) : (seq1 - seq2);
        
    }
}
