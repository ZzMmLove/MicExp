package cn.gdgst.palmtest.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 4/28 0028.
 */

public class CollectionData implements Serializable{

    /**
     *  "id":392,
     "member_id":"641",
     "doc_id":4228,
     "model":"Play",
     "name":"测量物体运动的平均速度",
     "dateline":1493340633
     */

    private String id;
    private String member_id ;
    private String doc_id;
    private String model;
    private String name;
    private String dateline;


    public CollectionData(String id, String member_id, String doc_id, String model, String name, String dateline) {
        this.id = id;
        this.member_id = member_id;
        this.doc_id = doc_id;
        this.model = model;
        this.name = name;
        this.dateline = dateline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    @Override
    public String toString() {
        return "CollectionData{" +
                "id='" + id + '\'' +
                ", member_id='" + member_id + '\'' +
                ", doc_id='" + doc_id + '\'' +
                ", model='" + model + '\'' +
                ", name='" + name + '\'' +
                ", dateline='" + dateline + '\'' +
                '}';
    }
}
