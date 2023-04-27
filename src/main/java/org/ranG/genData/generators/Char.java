package org.ranG.genData.generators;

import java.util.ArrayList;
import java.util.Random;

public class Char implements Generator{
    int length;
    StringBuilder sb;
    static String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public Char(int length){

        this.sb = new StringBuilder();
        this.length = length;
    }
    @Override
    public String gen() {
        Random random = new Random();
        for(int i=0;i<this.length;i++){
            sb.append(s.substring(random.nextInt(s.length()),random.nextInt(s.length())+1));
        }
        return  String.format("\"%s\"",sb.toString());

    }
}
