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
        GenAndPrefix(Generator gen,String prefix){
            this.gen = gen;
            this.prefix = prefix;
        }
    }
    ArrayList<GenAndPrefix>tplComponents = new ArrayList<>();




    public Temporal(int from,int to){
        this.from = from;
        this.to   = to;
        /* init the array */
        /*year */
        tplComponents.add(new GenAndPrefix(new Int(2000,2023,"%04d"),""));
        /*month*/
        tplComponents.add(new GenAndPrefix(new Int(1,12,"%02d"),"-"));
        /* day*/
        tplComponents.add(new GenAndPrefix(new Int(1,28,"%02d"),"-"));
        /*hour */
        tplComponents.add(new GenAndPrefix(new Int(0,23,"%02d")," "));
        /*minute*/
        tplComponents.add(new GenAndPrefix(new Int(0,59,"%02d"),":"));
        /* second*/
        tplComponents.add(new GenAndPrefix(new Int(0,59,"%02d"),":"));
        /* micro second */
        tplComponents.add(new GenAndPrefix(new Int(0,999999,""),"."));

    }

    @Override
    public String gen() {
        /* 2 5 */
        StringBuilder sb = new StringBuilder();
        sb.append(tplComponents.get(this.from).gen.gen());
        for(int i=this.from+1 ;i<=this.to;i++){
            GenAndPrefix genpre = tplComponents.get(i);
            sb.append(genpre.prefix+ genpre.gen.gen());
        }
        return "'" + sb.toString() + "'";
    }
}
