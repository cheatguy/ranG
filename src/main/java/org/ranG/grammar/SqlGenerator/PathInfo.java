package org.ranG.grammar.SqlGenerator;

public class PathInfo {
    ProductionSet   productionSet;
    SeqSet seqSet;
    public PathInfo(){
        productionSet = new ProductionSet();
        seqSet = new SeqSet();
    }
    public void clear(){
        productionSet.clear();
        seqSet.clear();
    }
}
