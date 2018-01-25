package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

/**
 *
 * Created by Administrator on 1/6 0006.
 */

public class UserVote implements Serializable{

    /**
     *  "id":"3975",
     "name":"666",
     "piao":"5",
     "remark":"adsas",
     "img_url_s":"./thumb_1.jpg",
     "video_url":"2.mp4"
     */

    private String id;
    private String name;
    private int piao;
    private String remark;
    private String img_url_s;
    private String video_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPiao() {
        return piao;
    }

    public void setPiao(int piao) {
        this.piao = piao;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    @Override
    public String toString() {
        return "UserVote{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", piao=" + piao +
                ", remark='" + remark + '\'' +
                ", img_url_s='" + img_url_s + '\'' +
                ", video_url='" + video_url + '\'' +
                '}';
    }
}
