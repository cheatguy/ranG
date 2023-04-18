package org.ranG.genData.generators;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.ranG.Main.yyPath;

public class English implements Generator{
    /* english from /resource/english.txt */
    ArrayList<String> dict;

    public English(){
        /* attention ：这里读取的文本换行符是/r/n(CRLF format) ，分割时候注意 */
        String content = readByte();
        String[] arr = content.split("\r\n");
        List<String> strList = Arrays.asList(arr);
        this.dict= new ArrayList<>(strList);
    }

    @Override
    public String gen() {
         Random rand = new Random();
         String odStr = this.dict.get(rand.nextInt(this.dict.size()));
         String newStr = odStr.replaceAll("/r","");
         return "\"" + newStr + "\"";
    }

    /* read english.txt file */
    public String readByte(){
        String englishPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\english.txt";
        try {
            String content = Files.readString(Paths.get(englishPath));
            return content;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
