package cn.gdgst.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "midbio_list".
 */
public class MidBio {

    private Long id;
    private String gid;
    private String cateid;
    private String gradeid;
    private String name;
    private String img_url;
    private String img_url_s;
    private String video_url;
    private String time;

    public MidBio() {
    }

    public MidBio(Long id) {
        this.id = id;
    }

    public MidBio(Long id, String gid, String cateid, String gradeid, String name, String img_url, String img_url_s, String video_url, String time) {
        this.id = id;
        this.gid = gid;
        this.cateid = cateid;
        this.gradeid = gradeid;
        this.name = name;
        this.img_url = img_url;
        this.img_url_s = img_url_s;
        this.video_url = video_url;
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

    public String getGradeid() {
        return gradeid;
    }

    public void setGradeid(String gradeid) {
        this.gradeid = gradeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}