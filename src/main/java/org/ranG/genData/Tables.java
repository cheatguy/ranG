package org.ranG.genData;

import java.util.HashMap;

public class Tables {



    class TableStmt{
        String format;
        String name;
        int rowNum;
        String ddl;
    }
    static VarWithDefault[] tableVars ={new VarWithDefault("rows",new String[]{"0", "1", "2", "10", "100"}),new VarWithDefault("charsets",new String[]{"undef"}),new VarWithDefault("partitions",new String[]{"undef"})};


}
