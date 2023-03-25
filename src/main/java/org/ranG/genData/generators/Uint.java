package org.ranG.genData.generators;

import java.util.Random;

public class Uint implements Generator{
    @Override
    public String gen() {
        Random rand = new Random();
        return Long.toString(Math.abs(rand.nextLong()));
    }
}
