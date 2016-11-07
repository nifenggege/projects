package com.feng.fresh.dp;

import com.feng.fresh.api.APIClient;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
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
        if(StringUtils.isNotEmpty(tokens[0]) && tokens[0].split("_")[0].equals(word)){
            return tokens[2];
        }

        for(int i=pos-3; i>0 && i<pos && i<dpList.size(); i++ ){
            tokens = dpList.get(i).split("(\\s)+");
            if(StringUtils.isNotEmpty(tokens[0])){
                String[] strs = dpList.get(pos).split("(\\s)+")[0].split("_");
                if(strs.length==2 && strs[1].equals(word)) {
                    return tokens[2];
                }
                else{
                    LOGGER.info("{}-{}-{}-{}", sentence, word, pos, Lists.newArrayList(strs));
                }
            }
        }

        for(int i=pos+1; i<pos+4 && i<dpList.size(); i++ ){
            tokens = dpList.get(i).split("(\\s)+");
            if(StringUtils.isNotEmpty(tokens[0])){
                String[] strs = dpList.get(pos).split("(\\s)+")[0].split("_");
                if(strs.length==2 && strs[1].equals(word)) {
                    return tokens[2];
                }
                else{
                    LOGGER.info("{}-{}-{}-{}", sentence, word, pos, Lists.newArrayList(strs));
                }
            }
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
        System.out.println(getDPType("以中药内服，西药注射，针刺加电针刺激，TDP理疗及自我按摩综合法治疗面瘫24例，治愈率100％", "治疗", 12));
    }
}
