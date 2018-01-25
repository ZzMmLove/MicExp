package cn.gdgst.palmtest.Entitys;

/**
 * Created by Administrator on 1/6 0006.
 */

public class PatyDetail {

    /**
     * "id":"1",
     "title":"2018年度第一届师生微客视频评选比赛",
     "endtime":"1517363343"
     */
    private int id;
    private String title;
    private String endtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    @Override
    public String toString() {
        return "PatyDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", endtime='" + endtime + '\'' +
                '}';
    }
}
