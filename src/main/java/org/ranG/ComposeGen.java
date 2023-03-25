package org.ranG;

import org.ranG.genData.generators.Generator;

import java.util.ArrayList;
import java.util.Random;

public class ComposeGen implements Generator {
    ArrayList<Generator> gs;
    public ComposeGen(ArrayList<Generator> gs){
        this.gs = gs;
    }

    @Override
    public String gen() {
        Random rand = new Random();
        return this.gs.get(rand.nextInt(this.gs.size())).gen();
    }
}
