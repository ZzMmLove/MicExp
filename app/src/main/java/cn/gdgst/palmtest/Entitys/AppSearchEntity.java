package cn.gdgst.palmtest.Entitys;

/**
 * Android客户端向服务器端搜索返回数据的实体类
 * Created by JenfeeMa on 2017/2/10.
 */

public class AppSearchEntity {
    private String id;
    private String model;
    private String title;
    private String url;

    public AppSearchEntity() {
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
                '}';
    }
}
