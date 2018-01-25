package cn.gdgst.palmtest.Entitys;

/**
 * Android客户端向服务器端搜索返回数据的实体类
 * Created by JenfeeMa on 2017/2/10.
 */

public class AppSearchEntity {

    /**
     * "id":"4207",
     "model":"Play",
     "title":"静电感应",
     "url":"/Public/Uploads/Video/20170301/58b640472bab6.mp4",
     "img":"/Public/Uploads/Video/20170301/thumb_58b6403c83c03.jpg"
     */

    private String id;
    private String model;
    private String title;
    private String url;
    private String img;

    public AppSearchEntity() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "AppSearchEntity{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
