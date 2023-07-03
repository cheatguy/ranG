package org.ranG.grammar.YaccParser;

import java.util.ArrayList;

/* one bnf expression */
public class Production {
    public int number; /* serial number */
    public Token head; /* left value of bnf */
    public ArrayList<Seq> alter; /* right expression in the bnf */

    /* also return a new Pnumber = pNumber + 1 */
    public Production(Token head,int pNumber){
        this.head = head;
        this.number = pNumber;
        alter = new ArrayList<>();
    }
    public void AppendSeq(Seq s){
        s.pNumber = this.number;
        s.sNumber = this.alter.size();
        this.alter.add(s);
    }

}
