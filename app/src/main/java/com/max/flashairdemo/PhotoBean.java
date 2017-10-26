package com.max.flashairdemo;

/**
 * Created by max on 17-10-26.
 */

public class PhotoBean {

    String fileName;
    String filePath;

    public PhotoBean(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
