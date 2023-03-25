package org.ranG.genData.generators;

import java.util.Random;

public class Int implements Generator {
    static int[] flag = {-1,1}  ;

    int min;
    int max;
    String tmpl;
    public Int(int max,int min,String tmpl){
        this.max = max;
        this.min = min;
        this.tmpl = tmpl;
    }
    @Override
    public String gen() {
        int intRes;
        Random rand = new Random();
        if(this.max < this.min){
            /* for `int` , pass 0,-1  */
            intRes = flag[rand.nextInt(flag.length)] * rand.nextInt(Integer.MAX_VALUE);
        }else{
            intRes = Common.randInRange(this.min,this.max);
        }
        if(tmpl.equals("")){
            return Integer.toString(intRes);
        }
        return String.format(this.tmpl,intRes);
    }
}
