package cn.gdgst.palmtest.bean;

import java.io.Serializable;

public class ZiXunEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6647446593558328211L;
    private String id;
    private String pid;
    private String title;
    private String img_url;
    private String img_url_s;
    private String video_url;
    private String content;
    private String view_count;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_url_s() {
        return img_url_s;
    }

    public void setImg_url_s(String img_url_s) {
        this.img_url_s = img_url_s;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
