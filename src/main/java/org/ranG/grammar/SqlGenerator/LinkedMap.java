package org.ranG.grammar.SqlGenerator;

import org.ranG.grammar.YaccParser.Parser;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkedMap {
    ArrayList<String> order;
    HashMap<String,Integer> m;
    public LinkedMap(){
        order = new ArrayList<>();
        m = new HashMap<>();
    }
    public void enter(String key){
        this.order.add(key);
        /*这里存在问题 */
        /* 原来的kv 值 +1 */
        if(this.m.containsKey(key)){
            this.m.put(key,this.m.get(key) + 1);
        }else{
            this.m.put(key,1); /* init as 0 ,but increase 1 */
        }

    }
    public void leave(String key){
        this.m.put(key,this.m.get(key) - 1);
        /*去除最右边的 */
        this.order.remove(this.order.size()-1);
    }
}
