package org.ranG.genData.generators;

import java.util.Random;
public class Common {
    static int randInRange(int start,int end){
        Random rand = new Random();
        return start + rand.nextInt(end-start+1);
    }
}
