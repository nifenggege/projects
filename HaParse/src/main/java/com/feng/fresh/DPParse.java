package com.feng.fresh;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 2016/10/16.
 */
public class DPParse {

    static Logger LOGGER = LoggerFactory.getLogger(DPParse.class);



    public static List<String> dpParse(File file){

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dpparse.txt")));
            List<String> list = Files.readLines(file, Charsets.UTF_8);
            Map<String, Object> map = Maps.newHashMap();
            map.put("api_key", "u8J0D7D7RtliIrZm9A8xRs8KBmKR9wcxopnAiFyo");
            map.put("pattern","dp");
            map.put("format","plain");
            for(String sentence : list){
                List<String> splitList = SentenceSplitTools.split(sentence);
                for(String token : splitList){
                    //String dpList = HttpClientAPI.get(token);
                    map.put("text", token);
                    String dpList = APIClient._get("http://api.ltp-cloud.com/analysis/",map);
                    bw.write(dpList);
                    bw.newLine();
                    bw.newLine();
                    LOGGER.info("dp解析结果："+dpList);
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO异常", e);
        } catch (Exception e) {
            LOGGER.error("异常", e);
        }finally {
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    LOGGER.error("异常", e);
                }
            }
        }
        return null;
    }
}
