package com.feng.fresh.dp;

import com.feng.fresh.api.APIClient;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 2016/10/16.
 */
public class DPParse {

    static Logger LOGGER = LoggerFactory.getLogger(DPParse.class);


    public static String getDPType(String sentence, String word, int pos){

        List<String> dpList = dpParse(sentence);
        if(pos<0 || pos>=dpList.size()) return "null";
        String[] tokens = dpList.get(pos).split("(\\s)+");
        if(tokens[0].split("_")[0].equals(word)){
            return tokens[2];
        }


        LOGGER.info("{}-{}-{}", sentence, word, pos);
        return null;
    }

    public static List<String> dpParse(String sentence){
        Map<String, Object> map = Maps.newHashMap();
        map.put("api_key", "u8J0D7D7RtliIrZm9A8xRs8KBmKR9wcxopnAiFyo");
        map.put("pattern","dp");
        map.put("format","plain");
        map.put("text", sentence);
        String dpList = null;
        try {
            dpList = APIClient._get("http://api.ltp-cloud.com/analysis/",map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lists.newArrayList(dpList.split("\n"));
    }

    public static void main(String[] args) {
        System.out.println(getDPType("方法：采用大连产WE2102—3型射频治疗机并TDP加电针治疗慢性前列腺炎35例，每次治疗40min，15次为l疗程，共2—3个疗程", "电针", 12));
    }
}
