package cn.gdgst.palmtest.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Administrator on 4/19 0019.
 */

public class FileUtils {

    /**
     * 视频文件转换成流 （字节数组流）
     * @param file   源文件
     * @return    返回一个字节数组输入流
     */
    public static ByteArrayInputStream getByteArrayInputStream(File file){
        return new ByteArrayInputStream(getBytesFromFile(file));
    }

    private static byte[] getBytesFromFile(File file) {
        FileInputStream is = null;
        //获取文件的大小
        long length = file.length();
        //创建一个数组来保存文件数据，长度最好与目标文件（这里是视频）的大小一样，这样的效率更高
        byte[] fileData = new byte[(int) length];
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int bytesRead = 0;
        //读取数据到byte数组中
        while (bytesRead != fileData.length){
            try {
                bytesRead += is.read(fileData, bytesRead, fileData.length - bytesRead);
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  fileData;

    }

}
