package cn.gdgst.entity;

import java.util.Date;

/**
 * Created by Jenfee on 2016/12/27.
 */

public class ExamPaper {
    /**
     * 试卷id
     */
    private int id;

    /**
     * 试卷名
     */
    private String paper;

    /**
     * 发布试卷的时间
     */
    private int addtime;

    public ExamPaper(int id, String paper, int addtime) {
        this.id = id;
        this.paper = paper;
        this.addtime = addtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    @Override
    public String toString() {
        return "ExamPaper{" +
                "id=" + id +
                ", paper='" + paper + '\'' +
                ", addtime=" + addtime +
                '}';
    }
}
