package cn.gdgst.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "wenku_list".
 */
public class WenKu {

    private Long id;
    private String gid;
    private String cateid;
    private String title;
    private String img_url;
    private String img_url_s;
    private String file_url;
    private String time;

    public WenKu() {
    }

    public WenKu(Long id) {
        this.id = id;
    }

    public WenKu(Long id, String gid, String cateid, String title, String img_url, String img_url_s, String file_url, String time) {
        this.id = id;
        this.gid = gid;
        this.cateid = cateid;
        this.title = title;
        this.img_url = img_url;
        this.img_url_s = img_url_s;
        this.file_url = file_url;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getCateid() {
        return cateid;
    }

    public void setCateid(String cateid) {
        this.cateid = cateid;
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

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "WenKu{" +
                "id=" + id +
                ", gid='" + gid + '\'' +
                ", cateid='" + cateid + '\'' +
                ", title='" + title + '\'' +
                ", img_url='" + img_url + '\'' +
                ", img_url_s='" + img_url_s + '\'' +
                ", file_url='" + file_url + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
