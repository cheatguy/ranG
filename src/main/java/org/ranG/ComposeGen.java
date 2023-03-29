package org.ranG;

import org.apache.logging.log4j.Logger;
import org.ranG.genData.LoggerUtil;
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
    public void  oneRow( ArrayList<String> row){
        Logger log = LoggerUtil.getLogger();
        if(this.gs.size() != row.size()){
            log.error("one row : record gen illegal");
        }
        for(int i =0;i<this.gs.size();i++){
            row.set(i,this.gs.get(i).gen());
        }
    }
}
