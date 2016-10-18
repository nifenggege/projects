package com.feng.fresh;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by feng on 2016/10/16.
 */
public class SentenceSplitTools {

    static Logger LOGGER = LoggerFactory.getLogger(SentenceSplitTools.class);

    public static final String SEPORETOR = "[。,!,?]";
    public static List<String> split(String sentence){

        Preconditions.checkArgument(StringUtils.isNotEmpty(sentence),"参数不能为空");

        return Lists.newArrayList(sentence.split(SEPORETOR));
    }

    public static void main(String[] args) {
        List<String> list = SentenceSplitTools.split("采用督脉电针电场治疗大鼠的半横断脊损伤，是一种简便、安全、疗效确实的治疗方法。本实验运用“联合行为记分法”，脊髓诱发电位及辣根过氧化酶逆行标记法作为观察指标。结果显示；接受督脉电针治疗组的大鼠ＣＢＳ值下降值明显高于对照组，ＳＣＥＰ潜伏期恢复较对照组明显改善，两者相比具有非常显著差异；组织学显示组的ＨＲＰ标记细胞显示多于对照组，三项检查指标联合评价证实：督脉地合电场能半横断脊髓神经轴突的再生。");
        LOGGER.info(list.size()+"");
    }
}
