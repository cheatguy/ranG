package org.ranG.genData;

import org.luaj.vm2.ast.Str;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigRet {
    ArrayList<String> ddls;
    HashMap<String,DdlFunc> keyFunc;
    public ConfigRet(ArrayList<String> ddls,HashMap<String,DdlFunc> keyFunc){
        this.ddls = ddls;
        this.keyFunc = keyFunc;
    }
}
