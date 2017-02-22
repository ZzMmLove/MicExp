package cn.gdgst.palmtest.imagecache;

import android.widget.ImageView;

/**
 * 功能描述：图片信息实体类
 *
 * @author android_ls
 */
public class ImageInfo {

	private int id; // 唯一标识
	private ImageView imageView; // 用于显示的组件
	private String url; // 网络URL
	private int width; // 宽度
	private int height; // 高度
	private boolean rounded; // 是否要转换成圆角
	private boolean compress; // 是否要进行质量压缩

	public ImageInfo(ImageView imageView, String url) {
		this.imageView = imageView;
		this.url = url;
	}

	public ImageInfo() {
	}

	public ImageInfo(ImageView imageView, String url, int width, int height,
					 boolean rounded, boolean compress) {
		this.imageView = imageView;
		this.url = url;
		this.width = width;
		this.height = height;
		this.rounded = rounded;
		this.compress = compress;
	}

	public ImageInfo(ImageView imageView, String url, boolean rounded) {
		this.imageView = imageView;
		this.url = url;
		this.rounded = rounded;
	}

	public ImageInfo(ImageView imageView, String url, int width, int height) {
		this.imageView = imageView;
		this.url = url;
		this.width = width;
		this.height = height;
	}

	public ImageInfo(ImageView imageView, String url, int width, int height,
					 boolean rounded) {
		this.imageView = imageView;
		this.url = url;
		this.width = width;
		this.height = height;
		this.rounded = rounded;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isRounded() {
		return rounded;
	}

	public void setRounded(boolean rounded) {
		this.rounded = rounded;
	}

}
