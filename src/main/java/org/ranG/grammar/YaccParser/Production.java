package org.ranG.grammar.YaccParser;

import java.util.ArrayList;

/* one bnf expression */
public class Production {
    int number; /* serial number */
    Token head; /* left value of bnf */
    ArrayList<Seq> alter; /* right expression in the bnf */

    /* also return a new Pnumber = pNumber + 1 */
    public Production(Token head,int pNumber){
        this.head = head;
        this.number = pNumber;
    }
    public void AppendSeq(Seq s){
        s.pNumber = this.number;
        s.sNumber = this.alter.size();
        this.alter.add(s);
    }

}
