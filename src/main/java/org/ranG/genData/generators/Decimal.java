package org.ranG.genData.generators;

import java.util.Random;

public class Decimal implements Generator{
    @Override
    public String gen() {
        Random rand = new Random();
        String intPart = Integer.toString(rand.nextInt(100));
        String decimalPart = String.format("%04d",(Common.randInRange(0,1999)));
        return intPart + "." + decimalPart;
    }
}
