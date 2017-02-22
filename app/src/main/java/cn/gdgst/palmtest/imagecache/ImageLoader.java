package cn.gdgst.palmtest.imagecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import cn.gdgst.palmtest.base.NetworkBaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 功能描述：加载（装载）图片
 *
 * 在以前，一个非常流行的内存缓存的实现是使用SoftReference or WeakReference ，但是这种办法现在并不推荐。
 * 从Android 2.3开始，垃圾回收器会更加积极的去回收软引用和弱引用引用的对象，这样导致这种做法相当的无效。
 * 另外，在Android 3.0之前，图片数据保存在本地内存中，它们不是以一种可预见的方式来释放的，
 * 这样可能会导致应用内存的消耗量出现短暂的超限，应用程序崩溃 。
 *
 * @author android_ls
 */
public class ImageLoader {

    /**
     * 内存缓存
     */
    private MemoryCache memoryCache;

    /**
     * 文件缓存
     */
    private FileCache fileCache;

    /**
     * 存放图片的显示视图ImageView和图片的URL
     */
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new LinkedHashMap<ImageView, String>());

    private List<AsyncBaseRequest> mAsyncRequests;

    private DefaultThreadPool mDefaultThreadPool;

    private Handler mHandler;

    public ImageLoader(NetworkBaseActivity activity) {
        this.memoryCache = new MemoryCache();
        this.fileCache = new FileCache(activity);
        this.mAsyncRequests = activity.getAsyncRequests();
        this.mDefaultThreadPool = activity.getDefaultThreadPool();
        this.mHandler = activity.getHandler();
    }

    /**
     * 加载图片
     * @param imgInfo 图片信息
     */
    public void displayImage(final ImageInfo imgInfo) {
        final ImageView imageView = imgInfo.getImageView();
        final String url = imgInfo.getUrl();

        imageViews.put(imageView, url);

        // 从内存缓存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            // 从文件缓存中查找
            final File file = fileCache.getFile(url);
            if (file.exists()) {
                String pathName = file.getAbsolutePath();
                System.out.println("pathName = " + pathName);
                System.out.println("file.length() = " + file.length());
                SimpleDateFormat mDateFormat = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss");
                System.out.println("file.lastModified() = " + mDateFormat.format(file.lastModified()));

                bitmap = BitmapFactory.decodeFile(pathName);
                imageView.setImageBitmap(bitmap);
            } else {
                // 开启线程加载图片
                try {
                    AsyncBaseRequest asyncRequest = new AsyncHttpGet(url, null, null, new ResultCallback() {
                        @Override
                        public void onSuccess(Object obj) {
                            if (obj == null || !(obj instanceof InputStream)) {
                                System.out.println("Loading image return Object is null or not is InputStream.");
                                return;
                            }
                            try {
                                // 根据指定的压缩比例，获得合适的Bitmap
                                Bitmap bitmap = BitmapUtil.decodeStream((InputStream) obj, imgInfo.getWidth(), imgInfo.getHeight());
                                if (imgInfo.isRounded()) {
                                    // 将图片变成圆角
                                    // bitmap = BitmapUtil.drawRoundCorner(bitmap, 8);
                                    bitmap = BitmapUtil.drawRoundBitmap(bitmap, 8);
                                }

                                if (imgInfo.isCompress()) {
                                    // 对Bitmap进行质量压缩
                                    bitmap = BitmapUtil.compressBitmap(bitmap);
                                }
                                // 将Bitmap转换成ByteArrayInputStream
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                                ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
                                // 将进行质量压缩后的数据写入文件（文件缓存）
                                fileCache.writeToFile(inStream, file);
                                // 存入内存缓存中
                                memoryCache.put(url, bitmap);
                                // 防止图片错位
                                String tag = imageViews.get(imageView);
                                if (tag == null || !tag.equals(url)) {
                                    System.out.println("tag is null or url and ImageView disaccord.");
                                    return;
                                }
                                // 用ImageView对象显示图片
                                final Bitmap btm = bitmap;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(btm);
                                    }
                                });
                            } catch (IOException e) {
                                // 这里不做处理，因为默认显示的图片在xml组件配置里已设置
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(int errorCode) {
                            System.out.println("Loading image error. errorCode = " + errorCode);
                        }
                    });
                    mDefaultThreadPool.execute(asyncRequest);
                    mAsyncRequests.add(asyncRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}