package org.ranG.genData.generators;

import org.luaj.vm2.ast.Str;

public class ConstGen implements Generator{
    /* as the default value for Generator */
    String contant;
    public ConstGen(String constant){
        this.contant = constant;
    }

    @Override
    public String gen() {
        return this.contant;
    }
}
