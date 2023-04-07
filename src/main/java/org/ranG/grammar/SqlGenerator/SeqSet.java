package org.ranG.grammar.SqlGenerator;

import org.ranG.grammar.YaccParser.Seq;

import java.util.ArrayList;
import java.util.HashMap;

public class SeqSet {
    ArrayList<Seq> seqs;
    HashMap<Int2,Boolean> set;
    public SeqSet(){
        seqs = new ArrayList<>();
        set = new HashMap<>();
    }
    public void add(Seq seq){
        if(!this.set.containsKey(new Int2(seq.pNumber,seq.sNumber))){
            this.set.put(new Int2(seq.pNumber,seq.sNumber),true);
            this.seqs.add(seq);
        }
    }
    public void clear(){
        seqs.clear();
        set.clear();
    }
}
