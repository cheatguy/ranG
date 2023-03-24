package org.ranG.genData.generators;

public class Temporal implements Generator {
    int from;
    int to;
    final static int yyyy = 0;
    final static int MM = 1;
    final static int dd = 2;
    final static int HH = 3;
    final static int mm = 4;
    final static int ss = 5;
    final static int SSS = 6;

    public Temporal(int from,int to){
        this.from = from;
        this.to   = to;
    }

    @Override
    public String gen() {
        return "";
    }
}
