package org.ranG.genData;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
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

    public void setDsn(String dsn){
        this.dsn = dsn;
    }
    /*
        read lua zz file
        exec zz file
     */
    public List<String> getDdl() {

        try{
            String zzStr ;
            if (zzPath.isEmpty()){
                zzPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\default.zz.lua";
            }else{
                Path filePath = Path.of(zzPath);
                zzStr = Files.readString(filePath);
                Globals globals = JsePlatform.standardGlobals();
                LuaValue code = globals.loadfile("D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\default.zz.lua");

            }

        }  catch (IOException e) {
            throw new RuntimeException(e);
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
