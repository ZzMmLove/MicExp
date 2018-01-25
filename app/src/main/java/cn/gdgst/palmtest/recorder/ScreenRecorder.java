/*
 * Copyright (c) 2014 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.gdgst.palmtest.recorder;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用于录屏功能的保存视频 // FIXME: 2017/3/3 JenfeeMa
 * @author Yrom
 */
public class ScreenRecorder extends Thread {
    private static final String TAG = "ScreenRecorder";

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private String mDstPath;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30; // 30 fps
    private static final int IFRAME_INTERVAL = 10; // 10 seconds between I-frames
    private static final int TIMEOUT_US = 10000;

    /**用于将音视频进行压缩编码，它有个比较牛X的地方是可以对Surface内容进行编码*/
    private MediaCodec mEncoder_MediaCodec;
    private Surface mSurface;
    /**用于将音频和视频进行混合生成多媒体文件*/
    private MediaMuxer mMuxer;
    private boolean mMuxerStarted = false;
    private int mVideoTrackIndex = -1;
    /**就是在多线程环境下，当有多个线程同时执行这些类的实例包含的方法时，具有排他性*/
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    /***/
    private VirtualDisplay mVirtualDisplay;

    public ScreenRecorder(int width, int height, int bitrate, int dpi, MediaProjection mp, String dstPath) {
        super(TAG);
        mWidth = width;
        mHeight = height;
        mBitRate = bitrate;
        mDpi = dpi;
        mMediaProjection = mp;
        mDstPath = dstPath;
    }


   /* public ScreenRecorder(MediaProjection mp) {
        // 480p 2Mbps
        this(640, 480, 2000000, 1, mp, "/sdcard/test.mp4");
    }*/

    /**
     * run()方法中完成了MediaCodec的初始化，VirtualDisplay的创建，以及循环进行编码的全部实现。
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
    }

    @Override
    public void run() {
        try {
            try {
                //第一步:准备编码器
                prepareEncoder();
                //第二步:创建mMuxer对象,mMuxer中封装了保存视频的路径,MediaMuxer构造函数：
                // 第一个参数：String path：指定媒体文件的输出路径， 第二个参数：输出媒体文件的格式
                mMuxer = new MediaMuxer(mDstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            /**
             * 利用MediaProjection实例创建虚拟屏幕，在OnActivityForResult()方法之后才调用createVirtualDisplay方法
             */
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display", mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            //第三步:录制虚拟展示
            recordVirtualDisplay();

        } finally {
            //释放资源
            release();
        }
    }

    /**
     * 编码器实现编码循环
     * 实现编码过程，由于使用的是Muxer来进行视频的采集，
     * 所以在resetOutputFormat方法中实际意义是将编码后的视频参数信息传递给Muxer并启动Muxer。
     */
    private void recordVirtualDisplay() {
        //是一个循环的过程
        while (!mQuit.get()) {
            int index = mEncoder_MediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
            Log.i(TAG, "dequeue output buffer index=" + index);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();

            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "retrieving buffers time out!");
                try {
                    // wait 10ms
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            } else if (index >= 0) {

                if (!mMuxerStarted) {
                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
                }
                encodeToVideoTrack(index);

                mEncoder_MediaCodec.releaseOutputBuffer(index, false);
            }
        }
    }

    /**
     * 开始跟踪视频堆栈
     * @param index
     */
    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData_ByteBuffer = mEncoder_MediaCodec.getOutputBuffer(index);

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.

            // 大致意思就是配置信息(avcc)已经在之前的resetOutputFormat()中喂给了Muxer，
            // 此处已经用不到了，然而在项目中这一步却是十分重要的一步，因为需要手动提前实现sps, pps的合成发送给流媒体服务器
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData_ByteBuffer = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }
        if (encodedData_ByteBuffer != null) {
            encodedData_ByteBuffer.position(mBufferInfo.offset);
            encodedData_ByteBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
            //保存视频数据
            mMuxer.writeSampleData(mVideoTrackIndex, encodedData_ByteBuffer, mBufferInfo);
            Log.i(TAG, "sent " + mBufferInfo.size + " bytes to muxer...");
        }
    }

    private void resetOutputFormat() {
        // 在接收缓冲之前，而且只能发生一次
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mEncoder_MediaCodec.getOutputFormat();

        Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());
        mVideoTrackIndex = mMuxer.addTrack(newFormat);
        mMuxer.start();
        mMuxerStarted = true;
        Log.i(TAG, "started media muxer, videoIndex=" + mVideoTrackIndex);
    }

    /**
     * 创建媒体编码解码器MediaCodec
     * 创建Surface表面或者是面板
     * 此方法中进行了编码器的参数配置与启动、Surface的创建两个关键的步骤
     * @throws IOException
     */
    private void prepareEncoder() throws IOException {

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        Log.d(TAG, "created video format: " + format);
        mEncoder_MediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder_MediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder_MediaCodec.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder_MediaCodec.start();

    }

    /**
     * 资源释放回收
     */
    private void release() {
        if (mEncoder_MediaCodec != null) {
            mEncoder_MediaCodec.stop();
            mEncoder_MediaCodec.release();
            mEncoder_MediaCodec = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }
}
