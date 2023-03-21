package org.ranG.genData;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Str;
import org.luaj.vm2.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LuaParser {

    /*extract vals  from two layers table by key1 and key2 */
    public ArrayList<String> extractSlice(LuaValue v, String key1 , String key2,String[] defaultValue){

        LuaValue vTable = DdlGenerator.globals.get(LuaValue.valueOf(key1));
        /* key2 会在外部迭代，不用担心读不到数据 */
        LuaValue subValue = vTable.get(LuaValue.valueOf(key2));
        ArrayList<String> content =new ArrayList<>();
        if(subValue.isnil()){  //没有取到值，使用默认值
            return new ArrayList<>(Arrays.asList(defaultValue));
        }

        LuaValue k = LuaValue.NIL;

        while(true){
            Varargs n = subValue.next(k);
            if ( (k = n.arg1()).isnil() )
                break;
            LuaValue v2 = n.arg(2);
            content.add(n.arg(2).toString());
        }

        return content;
    }

}
