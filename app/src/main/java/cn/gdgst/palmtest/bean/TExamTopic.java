package cn.gdgst.palmtest.bean;

import java.io.Serializable;

/**
 * 考评系统中的填空题类
 * Created by JenfeeMa on 2017/3/4.
 */

public class TExamTopic implements Serializable{
    /**
     * 填空题的题目
     */
    private String title;
    /**
     * 填空题的图片路径
     */
    private String img;
    /**
     * 填空题的正确答案
     */
    private String right;
    /**
     * 填空题的解析
     */
    private String jiexi;

    public TExamTopic() {

    }

    public TExamTopic(String img, String jiexi, String right, String title) {
        this.img = img;
        this.jiexi = jiexi;
        this.right = right;
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getJiexi() {
        return jiexi;
    }

    public void setJiexi(String jiexi) {
        this.jiexi = jiexi;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TExamTopic{" +
                "img='" + img + '\'' +
                ", title='" + title + '\'' +
                ", right='" + right + '\'' +
                ", jiexi='" + jiexi + '\'' +
                '}';
    }
}
