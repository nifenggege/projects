package com.feng.fresh.train;

import com.feng.fresh.lineprocessor.EventProcessor;
import com.feng.fresh.model.Event;
import com.feng.fresh.model.EventEnum;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 2016/8/25.
 */
public class EventParser {

    static final Logger LOGGER = LoggerFactory.getLogger(EventParser.class);

    private static Map<EventEnum, Map<String, Integer>> triggerMap = Maps.newHashMap();

    private static List<List<String>> sentenceTriggerList = Lists.newArrayList();

    public  static Map<String, EventEnum> word2typeMap = Maps.newHashMap();

    static{
       /* String path = EventParser.class.getClassLoader().getResource("train/train-arg.txt").getPath();
        File file = new File(path);
        EventParser.parseEvent(file);*/
    }

    /**
     * 根据训练语料获取触发词（事件）
     * @param file
     * @return
     */
    public static Map<EventEnum, Map<String, Integer>> parseEvent(File file){

        if(null==file || !file.exists())  return null;

        LOGGER.debug("开始处理文件：{}", file.getName());
        EventProcessor linePorcessor = new EventProcessor();
        try {
            Files.readLines(file, Charsets.UTF_8, linePorcessor);
        } catch (IOException e) {
            LOGGER.error("读取文件失败", e);
        }
        triggerMap = linePorcessor.getResult();
        sentenceTriggerList = linePorcessor.getSentenceTriggerList();
        word2typeMap = linePorcessor.getWord2typeMap();
        LOGGER.info(linePorcessor.getSentenceTriggerList().toString());
        return triggerMap;
    }

    public static Map<EventEnum, Map<String, Integer>> getTriggerMap() {
        return triggerMap;
    }

    public static List<List<String>> getSentenceTriggerList() {
        return sentenceTriggerList;
    }

    public static Map<String, EventEnum> getWord2typeMap() {
        return word2typeMap;
    }
}
