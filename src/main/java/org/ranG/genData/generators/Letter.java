package org.ranG.genData.generators;

import java.util.Random;
public class Letter implements Generator{
    @Override
    public String gen() {
        Random random = new Random();
        char randomChar = (char) (random.nextInt(26) + 'a');
        return String.format("\"%c\"", randomChar);
    }
}
