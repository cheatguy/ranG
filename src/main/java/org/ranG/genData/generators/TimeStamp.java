package org.ranG.genData.generators;

public class TimeStamp implements Generator{

    @Override
    public String gen() {
        return String.format("%04d%02d%02d%02d%02d%02d",Common.randInRange(2000,2019),Common.randInRange(1,12),Common.randInRange(1,28),Common.randInRange(0,23),Common.randInRange(0,59),Common.randInRange(0,59));
    }
}
