package org.ranG.genData.generators;

import java.util.ArrayList;
import java.util.Random;

public class Char implements Generator{
    int length;
    byte[] randChars;

    public Char(int length){
        String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        this.randChars = s.getBytes();
        this.length = length;
    }
    @Override
    public String gen() {
        ArrayList<Byte> b = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<this.length;i++){
            b.add(this.randChars[random.nextInt(randChars.length)]);
        }

        byte[] byteArray = new byte[this.length];
        for(int i=0;i<this.length;i++){
            byteArray[i]=b.get(i);
        }

        return  String.format("\"%s\"",new String(byteArray));

    }
}
