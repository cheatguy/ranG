package org.ranG.genData;

import java.util.HashMap;
import org.ranG.genData.generators.*;

public class Data {
    static final String numberType = "numbers";
    static final String blobType ="blobs";
    static final String temporalType = "temporas";
    static final String enumType = "enum";
    HashMap<String,Generator> gens;
    static HashMap<String,String> summaryType = new HashMap<>(){{
        put("int",numberType);
        put("bigint",numberType);
        put("float",numberType);
        put("double",numberType);
        put("decimal",numberType);
        put("numeric",numberType);
        put("fixed",numberType);
        put("bool",numberType);
        put("bit",numberType);

        put("blob",blobType);
        put("text",blobType);
        put("binary",blobType);

    }};

}
