package com.feng.fresh;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by feng on 2016/10/16.
 */
public class HttpClientAPI {

    static Logger LOGGER = LoggerFactory.getLogger(HttpClientAPI.class);
    static HttpClient httpClient = new HttpClient();

    public static String get(String sentence){
        LOGGER.info("处理句子,{}", sentence);
        String url = buildURL(sentence);
        return connection(url);
    }


    private static String buildURL(String sentence) {
        String url = "http://api.ltp-cloud.com/analysis/?api_key=u8J0D7D7RtliIrZm9A8xRs8KBmKR9wcxopnAiFyo&pattern=dp&format=plain";
        String encodeStr = "";
        try {
            encodeStr = URLEncoder.encode(sentence, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("URL编码出错");
        }
        url = url+ "&text="+encodeStr;
        LOGGER.info("url={}",url);
        return url+ "&text="+encodeStr;
    }

    private static String connection(String url) {

        GetMethod getMethod = new GetMethod(url);

        String result = null;

        try {

            int statusCode = httpClient.executeMethod(getMethod);

            InputStream inputStream = getMethod.getResponseBodyAsStream();
            byte[] buff = new byte[1024];
            int size = 0;
            StringBuffer sb = new StringBuffer();
            while((size=inputStream.read(buff))!=-1){
                sb.append(new String(buff, 0, size));
            }
            result = sb.toString();

        }catch (HttpException e) {
            LOGGER.error("get请求出错", e);

        } catch (IOException e) {
            LOGGER.error("IO异常", e);
        } finally {
            getMethod.releaseConnection();
        }

        return result;
    }

    public static void main(String[] args){
        try {
            String encodeText = URLEncoder.encode("本文通过用２６号５寸长毫针深斜刺关元穴治疗８６例尿潴留症的临床观察：认为尿潴留膀胱过度充盈，针刺下腹部穴位时，可以深刺或深斜刺，运针以间隙动留针为主，不宜久留针或电针刺激，并运用一系列的解剖和神经肌肉的生理特性进行分析。","utf-8");
            String url = "http://api.ltp-cloud.com/analysis/?api_key=u8J0D7D7RtliIrZm9A8xRs8KBmKR9wcxopnAiFyo&text="+encodeText+"&pattern=dp&format=plain";
            LOGGER.info(HttpClientAPI.get(url));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("URL编码异常", e);
        }

    }
}
