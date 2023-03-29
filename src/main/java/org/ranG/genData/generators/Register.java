package org.ranG.genData.generators;

import javax.imageio.metadata.IIOInvalidTreeException;
import java.sql.Time;
import java.util.HashMap;

public class Register {
    static HashMap<String,Generator> gMap = new HashMap<>();

    public Register(){
        gMap.put("digit", new Digit());
        gMap.put("letter",new Letter());

        /* temporal
            yyyy-MM-dd HH:mm:ss.SSS
         */
        gMap.put("data",new Temporal(Temporal.yyyy,Temporal.dd));
        gMap.put("year",new Temporal(Temporal.yyyy,Temporal.yyyy));
        gMap.put("month",new Temporal(Temporal.MM,Temporal.MM));
        gMap.put("day",new Temporal(Temporal.dd,Temporal.dd));
        gMap.put("hour",new Temporal(Temporal.HH,Temporal.HH));
        gMap.put("minute",new Temporal(Temporal.mm,Temporal.mm));
        gMap.put("second",new Temporal(Temporal.ss,Temporal.ss));
        gMap.put("microsecond",new Temporal(Temporal.SSS,Temporal.SSS));
        gMap.put("time",new Temporal(Temporal.HH,Temporal.ss));
        gMap.put("datetime",new Temporal(Temporal.yyyy,Temporal.ss));
        gMap.put("second_microsecond",new Temporal(Temporal.ss,Temporal.SSS));
        gMap.put("minute_microsecond",new Temporal(Temporal.mm,Temporal.SSS));
        gMap.put("minute_second",new Temporal(Temporal.mm,Temporal.ss));
        gMap.put("hour_microsecond",new Temporal(Temporal.HH,Temporal.SSS));
        gMap.put("hour_second",new Temporal(Temporal.HH,Temporal.ss));
        gMap.put("hour_minute",new Temporal(Temporal.HH,Temporal.mm));
        gMap.put("day_microsecond",new Temporal(Temporal.dd,Temporal.SSS));
        gMap.put("day_second",new Temporal(Temporal.dd,Temporal.ss));
        gMap.put("day_minute",new Temporal(Temporal.dd,Temporal.mm));
        gMap.put("day_hour",new Temporal(Temporal.dd,Temporal.HH));
        gMap.put("year_month",new Temporal(Temporal.yyyy,Temporal.MM));

        gMap.put("timestamp",new TimeStamp()); /* 这里没有对时间戳进行赋值，在gen（） 中存在生成时间*/
        /* todo : english 这部分太麻烦了 ，暂时不做 */
        gMap.put("english",new Temporal(Temporal.dd,Temporal.SSS));

        gMap.put("char",new Char(10));
        gMap.put("bool",new Int(0,1,""));
        gMap.put("boolean",new Int(0,1,""));
        gMap.put("tinyint",new Int(-128,127,""));
        gMap.put("tinyint_unsigned",new Int(0,255,""));
        gMap.put("smallint",new Int(-32768,32767,""));
        gMap.put("smallint_unsigned",new Int(0,65535,""));
        gMap.put("mediumint",new Int(-8388608,8388607,""));
        gMap.put("mediumint_unsigned",new Int(0,16777215,""));
        gMap.put("bigint",new BigInt(false));
        gMap.put("bigint_unsigned",new BigInt(true));
        gMap.put("int",new Int(0,-1,""));
        gMap.put("int_unsigned",new Uint());
        gMap.put("integer",new Int(0,-1,""));
        gMap.put("decimal",new Decimal());




    }


    public Generator get(String name){
        Generator generator;
        if(gMap.containsKey(name)){
            generator = gMap.get(name);
        }else{
            /* 使用默认值 */
            generator = new ConstGen();
        }

        /* if gmap dosen't find the type ? */
        return generator;
    }
}
