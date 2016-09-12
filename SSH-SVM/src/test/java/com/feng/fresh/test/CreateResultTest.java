package com.feng.fresh.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by feng on 2016/9/12.
 */
public class CreateResultTest {
    @Test
    public void resultFactory() throws Exception {

        String path = this.getClass().getClassLoader().getResource("seq/test-seq.txt").getPath();
        File file = new File(path);
        Assert.assertTrue(null!=file && file.exists());
        CreateResult.resultFactory(file);
    }

}