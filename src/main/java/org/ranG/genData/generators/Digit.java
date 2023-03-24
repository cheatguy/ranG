package org.ranG.genData.generators;

import java.util.Random;

public class Digit implements Generator {
    @Override
    public String gen() {
        Random random = new Random();
        int r = random.nextInt(10);
        return Integer.toString(r);
    }
}
