package cn.piesat.waterconservation.utils;

import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

import cn.piesat.waterconservation.WCApplication;

/**
 * 写入TXT文件
 * Created by sen.luo on 2018/7/2.
 */

public class FileWriteUtils {
    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                LogUtils.show("文件夹不存在", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            ToastUtil.showShort(WCApplication.getInstance(),"坐标写入TXT文本成功");
            raf.close();
        } catch (Exception e) {
            Log.e("写入文件error", "Error on write File:" + e);
        }
    }



    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            LogUtils.show("生成文件error:", e + "");
        }
    }

}
