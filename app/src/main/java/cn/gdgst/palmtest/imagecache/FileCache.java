package cn.gdgst.palmtest.imagecache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.os.StatFs;

/**
 * 功能描述：网络下载文件本地缓存类
 *
 * @author android_ls
 */
public class FileCache {

    /**
     * 本地与我们应用程序相关文件存放的根目录
     */
    private static final String ROOT_DIR_PATH = "CopyEveryone";

    /**
     * 下载文件存放的目录
     */
    private static final String IMAGE_DOWNLOAD_CACHE_PATH = ROOT_DIR_PATH + "/Download/cache";

    /**
     * 默认的磁盘缓存大小(20MB)
     */
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 20;

    /**
     * 缓存文件存放目录
     */
    private File cacheDir;

    /**
     * 缓存根目录
     */
    private String cacheRootDir;

    private Context mContext;

    public FileCache(Context context) {
        mContext = context;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheRootDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            cacheRootDir = mContext.getCacheDir().getAbsolutePath();
        }

        cacheDir = new File(cacheRootDir + File.separator + IMAGE_DOWNLOAD_CACHE_PATH);
        // 检测文件缓存目录是否存在，不存在则创建
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    /**
     * 获取下载的文件要存放的缓存目录
     * /mnt/sdcard/CopyEveryone/Download/cache
     * @return 缓存目录的全路径
     */
    public String getCacheDirPath() {
        return cacheDir.getAbsolutePath();
    }

    /**
     * 根据URL从文件缓存中获取文件
     * @param url url的hashCode为缓存的文件名
     */
    public File getFile(String url) {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        String filename = String.valueOf(url.hashCode());
        File file = new File(cacheDir, filename);
        return file;
    }

    /**
     * 计算存储可用的大小
     * @return
     */
    @SuppressWarnings("deprecation")
    public long getAvailableMemorySize() {
        StatFs stat = new StatFs(cacheRootDir);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 将指定的数据写入文件
     * @param inputStream InputStream
     * @param outputStream OutputStream
     * @throws IOException
     */
    public synchronized void writeToFile(InputStream inputStream, File file) throws IOException {
        int fileSize  = inputStream.available();
        System.out.println("fileSize = " + fileSize);
        long enabledMemory  = getAvailableMemorySize();
        System.out.println("当前可用硬盘: " + (enabledMemory/1024/1024)); // 单位：MB
        // 当前可用存储空间不足20M
        if(DEFAULT_DISK_CACHE_SIZE > enabledMemory){
            if (fileSize > enabledMemory) {
                // 检测可用空间大小，若不够用则删除最早的文件
                File[] files = cacheDir.listFiles();
                Arrays.sort(files, new FileLastModifSort());
                int length = files.length;
                for (int i = 0; i < length; i++) {
                    files[i].delete();
                    length = files.length;
                    enabledMemory  = getAvailableMemorySize();
                    System.out.println("当前可用内存: " + enabledMemory);
                    if (fileSize <= enabledMemory) {
                        System.out.println("够用了，不用在删除了");
                        break;
                    }
                }
            }
        } else {
            int count = 0;
            File[] files = cacheDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                count += files[i].length();
            }
            System.out.println("file cache size = " + count);
            // 使用的空间大于上限
            enabledMemory = DEFAULT_DISK_CACHE_SIZE - count;
            if(fileSize > enabledMemory){
                Arrays.sort(files, new FileLastModifSort());
                int length = files.length;
                for (int i = 0; i < length; i++) {
                    count -= files[i].length();
                    files[i].delete();
                    length = files.length;
                    enabledMemory = DEFAULT_DISK_CACHE_SIZE - count;
                    if (fileSize <= enabledMemory) {
                        System.out.println("够用了，不用在删除了");
                        break;
                    }
                }
            }
        }

        if(enabledMemory == 0){
            return;
        }
        // 将数据写入文件保存
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.flush();
        outStream.close();
        inputStream.close();

        // 设置最后修改的时间
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
        System.out.println("file.length() = " + file.length());
        SimpleDateFormat mDateFormat = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss");
        System.out.println("writeToFile file.lastModified() = " + mDateFormat.format(file.lastModified()));
    }

    /**
     * 根据文件的最后修改时间进行排序
     * @author android_ls
     *
     */
    class FileLastModifSort implements Comparator<File> {
        public int compare(File file1, File file2) {
            if (file1.lastModified() > file2.lastModified()) {
                return 1;
            } else if (file1.lastModified() == file2.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    /**
     * 清空缓存的文件
     */
    public void clear() {
        if (!cacheDir.exists()) {
            return;
        }
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

}