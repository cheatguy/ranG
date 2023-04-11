package org.ranG.grammar.YaccParser;

public class RuneSeq {
    char[] rune;
    int pos;
    public RuneSeq(char[] rune,int pos){
        this.rune = rune;
        this.pos  = pos;
    }
    /* return 0 means err */
    public char readRune(){
        if(this.pos>= this.rune.length){
            return 0;
        }
        char cur = this.rune[this.pos];
        this.pos ++;
        return cur;
    }
    public void unReadRune(){
        this.pos --;
    }
    public void setPos(int pos){
        this.pos = pos;
    }
    public boolean peekEqual(char expect){
        if(this.pos >= this.rune.length){
            return false;
        }
        return this.rune[this.pos] == expect;
    }
    public boolean lastEqual(char expect){
        if(this.pos <= 1){
            return false;
        }
        return this.rune[this.pos -2 ] == expect;
    }
    public String slice(int from){
        return this.rune.toString().substring(from,this.pos);
    }

}
