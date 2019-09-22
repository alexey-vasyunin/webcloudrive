package ru.vasyunin.springcloudrive.utils;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class FileUtilsTest {

    @Test
    void getFileExtension1() {
        Assert.assertEquals("txt", FileUtils.getFileExtension("test.filename.txt"));
    }

    @Test
    void getFileExtension2() {
        Assert.assertEquals("", FileUtils.getFileExtension("Makefile"));
    }

    @Test
    void getFileExtension3() {
        Assert.assertEquals("", FileUtils.getFileExtension("."));
    }

    @Test
    void getFileExtension14() {
        Assert.assertEquals("jpg", FileUtils.getFileExtension(Paths.get("test.filename.jpg")));
    }


}