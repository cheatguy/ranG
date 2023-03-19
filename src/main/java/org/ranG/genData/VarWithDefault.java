package org.ranG.genData;

import org.luaj.vm2.ast.Str;

public class VarWithDefault {
    String name;
    String[] defaultValue;
    VarWithDefault(String name,String[] defaultValue){
        this.name = name;
        this.defaultValue = defaultValue;
    }
}
