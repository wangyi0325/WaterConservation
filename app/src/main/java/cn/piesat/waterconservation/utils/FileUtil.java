package cn.piesat.waterconservation.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sen.luo on 2018/6/26.
 */

public class FileUtil {

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }


    /**
     * 获取指定文件夹下的所有子文件夹名称
     *
     * @param filePath
     * @return
     */
    public static List<String> getDirectoryNames(String filePath) {
        List<String> filesList = new ArrayList<String>();
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tempFile : files) {
                if (tempFile.isDirectory()) {
                    filesList.add(tempFile.getName());
                }
            }
            return filesList;
        }

        return null;
    }



    /**
     * 判断某文件夹中是否存在某格式的文件
     *
     * @param directory  文件夹路径
     * @param except     不包含这些文件夹
     * @param fileSuffixs 文件后缀名
     * @return
     */
    public static List<File> hasFileBySuffix(String directory, List<String> except, List<String> fileSuffixs) {
        List<File> goalFiles = new ArrayList<>();
        File file = new File(directory);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tempFile : files) {
                if (except == null || !except.contains(tempFile.getName())) {
                    if (tempFile.isFile()) {
                        String fileName = tempFile.getName();
                        try {
                            String hz = fileName.substring(fileName.lastIndexOf("."));
                            if(fileSuffixs.contains(hz)){
                                goalFiles.add(tempFile);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (tempFile.isDirectory()) {
                        List<File> childFiles = hasFileBySuffix(tempFile.getAbsolutePath(), null, fileSuffixs);
                        if (childFiles != null) {
                            goalFiles.addAll(childFiles);
                        }
                    }
                }
            }
        }
        return goalFiles;
    }


    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }
    }


    /**
     * 判断是否安装APP
     * @param mContext
     * @param packagename
     * @return
     */
    public static boolean isPackageInstalled(Context mContext, String packagename) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        } finally {
            return packageInfo == null ? false : true;
        }
    }


}
