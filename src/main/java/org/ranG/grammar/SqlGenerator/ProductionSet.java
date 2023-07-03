package org.ranG.grammar.SqlGenerator;

import org.ranG.grammar.YaccParser.Production;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductionSet {
    ArrayList<Production> productions;
    HashMap<Integer,Boolean> set;
    public ProductionSet(){
        productions = new ArrayList<>();
        set = new HashMap<>();
    }
    public void add(Production production){
        if(!set.containsKey(production.number)){
            this.set.put(production.number,true);
            this.productions.add(production);
        }
    }
    public void clear(){
        this.productions.clear();
        set.clear();
    }

}
