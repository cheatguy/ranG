package org.ranG.genData.generators;



import java.util.ArrayList;

public class English implements Generator{
    /* english from /resource/english.txt */
    ArrayList<String> dict;

    public English(String name){
        String cannonicalName = name.replaceAll("\\","/" ) ;
    }

    @Override
    public String gen() {
        return null;
    }
}
