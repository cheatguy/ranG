package org.ranG.grammar.YaccParser;

import java.util.ArrayList;

/* token seqence of branch */
public class Seq {
    public ArrayList<Token> items;
    public int pNumber;
    public int sNumber;
    public Seq(ArrayList<Token> items){
        if(items == null){
            this.items = new ArrayList<>();
        }else{
            this.items = items;
        }


    }
    public String getString(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<this.items.size();i++){
            Token tkn = this.items.get(i);
            if(i==0){
                sb.append(tkn.originString());
                continue;
            }
            if (tkn.hasPreSpace()){
                sb.append(" ");
            }
            sb.append(tkn.originString());
        }
        return sb.toString();
    }
}
