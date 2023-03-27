package org.ranG.genData;

import org.luaj.vm2.LuaValue;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TableHandler extends Tables implements Handler{

    ArrayList<String> fields;
    public TableHandler(ArrayList<String> fields){
        this.buf = ByteBuffer.allocate(2048);
        this.m = new ArrayList<>();
        this.stmts = new ArrayList<>();
        this.fields = fields;
    }
    @Override
    public int anonFunc(ArrayList<String> cur) {
        this.buf.reset();
        byte[] str = Tables.tNamePrefix.getBytes();
        this.buf.put(str);
        TableStmt stmt = new TableStmt();
        for(int i=0;i< cur.size();i++){
            /*
                field name  : fields[s]
                field value : cur[s]
             */
            this.fields.get(i);
            String putTmp = "_"+ cur.get(i);
            this.buf.put(putTmp.getBytes());

        }
        return 0;
    }
}
