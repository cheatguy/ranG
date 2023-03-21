package org.ranG.genData;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;

import static org.ranG.Main.zzPath;

public class DdlGenerator {
    String dsn;
    /* double? ,not sure this global can be used in LuaParser */
    static Globals globals = JsePlatform.standardGlobals();
    public void setDsn(String dsn){
        this.dsn = dsn;
    }
    /*
        read lua zz file
        exec zz file
     */
    public List<String> getDdl() {


        String zzStr ;
        if (zzPath.isEmpty()){
            zzPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\default.zz.lua";
        }else{
            Path filePath = Path.of(zzPath);

            LuaValue code = globals.loadfile(zzPath).call();
            Tables tb = new Tables("","tables",code);
            Fields fd = new Fields("","fields",code);
            System.out.println("test");

            /*iter the value */
//                LuaValue k = LuaValue.NIL;
//                while(true){
//                    Varargs n = vRow.next(k);
//                    if ( (k = n.arg1()).isnil() )
//                        break;
//                    LuaValue v = n.arg(2);
//                    System.out.println(v.toint());
//                }





        }







        List<String> ddls = new ArrayList<>();
        ddls.add("tr");
        return  ddls;

    }
    public void act(){
        /*包含生成ddl语句 ， 连接DB ， 执行 ddl语句 */
        List<String> dlls = getDdl();
    }

}
