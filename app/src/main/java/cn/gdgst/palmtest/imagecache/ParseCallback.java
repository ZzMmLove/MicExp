package cn.gdgst.palmtest.imagecache;

/**
 * 功能描述：网络请求返回的数据解析接口
 * @author android_ls
 *
 */
public interface ParseCallback {
    /**
     * 对网络请求返回的数据进行解析
     * @param str 要解析的字符串，JSON格式或XML
     * @return 解析的结果
     */
    public Object parse(String str);
}