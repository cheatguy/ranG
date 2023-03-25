package org.ranG.genData.generators;

import java.util.Random;

public class BigInt implements Generator {
    boolean unsigned;
    long[] flags;

    public BigInt(boolean unsigned){
        this.unsigned = unsigned;
        this.flags = new long[] {-1,1};
    }
    @Override
    public String gen() {
        Random rand = new Random();
        if(this.unsigned){
            return Long.toString(Math.abs(rand.nextLong()));
        }else{
            long flag = this.flags[rand.nextInt(flags.length)];
            /* potential bug */
            return Long.toString(flag * rand.nextLong());
        }
    }
}
