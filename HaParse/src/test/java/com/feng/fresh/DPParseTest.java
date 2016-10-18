package com.feng.fresh;


import org.junit.Test;

import java.io.File;

/**
 * Created by feng on 2016/10/16.
 */
public class DPParseTest {

    @Test
    public void dpParse() throws Exception {

        String path = this.getClass().getClassLoader().getResource("noLabelFile.txt").getPath();
        File file = new File(path);
        DPParse.dpParse(file);
    }

}