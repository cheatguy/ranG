package org.ranG.genData;

import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FullTestor {
    String dsn;
    int queryNum = 1000;
    static Logger log = LoggerUtil.getLogger();
    Map<String, Function<String, String>> functionMap = new HashMap<>();
    static Connection conn;
    ArrayList<String> randomSqls;
    public FullTestor(String dsn){
        this.dsn = dsn;
        this.randomSqls= new ArrayList<>();  /* 初始化一个存值的 arr */
    }

    public void act(){
        Logger log = LoggerUtil.getLogger();
        DdlGenerator generator = new DdlGenerator();
        generator.setDsn(this.dsn);
        generator.act();
        SqlGenerator sqlGenerator = new SqlGenerator(this.dsn,1000);
        sqlGenerator.act();
    }

}
