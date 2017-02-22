package cn.gdgst.palmtest.imagecache;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 功能描述：内存缓存类
 *
 * @author android_ls
 */
public class MemoryCache {

    /**
     * 打印LOG的TAG
     */
    private static final String TAG = "MemoryCache";

    /**
     * 放入缓存时是个同步操作
     * LinkedHashMap构造方法的最后一个参数true代表这个map里的元素将按照最近使用次数由少到多排列，
     * 这样的好处是如果要将缓存中的元素替换，则先遍历出最近最少使用的元素来替换以提高效率
     */
    private Map<String, Bitmap> cacheMap = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    // 缓存只能占用的最大堆内存
    private long maxMemory;

    public MemoryCache() {
        // 使用25%的可用的堆大小
        maxMemory = Runtime.getRuntime().maxMemory() / 4;
        Log.i(TAG, "MemoryCache will use up to " + (maxMemory / 1024 / 1024) + "MB");
    }

    /**
     * 根据key获取相应的图片
     * @param key
     * @return Bitmap
     */
    public Bitmap get(String key) {
        if (!cacheMap.containsKey(key)){
            return null;
        }
        return cacheMap.get(key);
    }

    /**
     * 添加图片到缓存
     * @param key
     * @param bitmap
     */
    public synchronized void put(String key, Bitmap bitmap) {
        checkSize();
        cacheMap.put(key, bitmap);
        Log.i(TAG, "cache size=" + cacheMap.size() + " bitmap size = " +  getBitmapSize(bitmap));
    }

    /**
     * 严格控制堆内存，如果超过将首先替换最近最少使用的那个图片缓存
     */
    private void checkSize() {
        long count = 0;
        Iterator<Entry<String, Bitmap>> iterator = cacheMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Bitmap> entry = iterator.next();
            count += getBitmapSize(entry.getValue());
        }

        Log.i(TAG, "cache size=" + count + " length=" + cacheMap.size());

        if (count > maxMemory) {
            while (iterator.hasNext()) {
                Entry<String, Bitmap> entry = iterator.next();
                count -= getBitmapSize(entry.getValue());
                iterator.remove();
                if (count <= maxMemory) {
                    System.out.println("够用了，不用在删除了");
                    break;
                }
            }
            Log.i(TAG, "Clean cache. New size " + cacheMap.size());
        }
    }

    /**
     * 获取bitmap的字节大小
     * @param bitmap
     * @return
     */
    private long getBitmapSize(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 清空缓存
     */
    public void clear() {
        cacheMap.clear();
    }
}