package com.feng.fresh.svm;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by feng on 2016/11/2.
 */
public class CreateSVMResultTest {
    @Test
    public void resultFactory() throws Exception {

        String path = this.getClass().getClassLoader().getResource("seq/test-seq.txt").getPath();
        File file = new File(path);
        Assert.assertTrue(null!=file && file.exists());
        CreateSVMResult.resultFactory(file);
    }

    @Test
    public void resultFactorytrain() throws Exception {

        String path = this.getClass().getClassLoader().getResource("seq/train-seq.txt").getPath();
        File file = new File(path);
        Assert.assertTrue(null!=file && file.exists());
        CreateSVMResult.resultFactory(file);
    }

}