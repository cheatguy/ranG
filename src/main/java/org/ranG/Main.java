package org.ranG;

import org.ranG.genData.*;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
    public static String zzPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\default.zz.lua";
//    public static String zzPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\test.lua";

    public static void main(String[] args) {

            Logger log = LoggerUtil.getLogger();

            try{

            Options options = new Options();

            Option genData = new Option("g1","genData",true,"generate table and data");

            Option genTable = new Option("g2","genSql",false,"generate sql statement based on table structure");
            options.addOption(genData);
            options.addOption(genTable);
            log.info("after the command line parsing");
            BasicParser parser = new BasicParser();
            CommandLine cl = parser.parse(options, args);
            if (cl.hasOption("genData")){
                String dsns = cl.getOptionValue("genData");
                DdlGenerator generator = new DdlGenerator();
                generator.setDsn(dsns);

                generator.act();

            }else if (cl.hasOption("genSql")){

            }else {
                System.out.println("not found");
            }
        } catch ( ParseException e){
            e.printStackTrace();
        }

    }
}