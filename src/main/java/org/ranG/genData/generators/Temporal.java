package org.ranG.genData.generators;

import java.util.ArrayList;

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
    class GenAndPrefix{
        Generator gen;
        String prefix;
    }
    static ArrayList<GenAndPrefix>tplComponents = new ArrayList<>();
//    static{
//        tplComponents.add(new Int(2000,2023,"%.4d"));
//    }

    public Temporal(int from,int to){
        this.from = from;
        this.to   = to;
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder();
//        sb.append()
        return "";
    }
}
