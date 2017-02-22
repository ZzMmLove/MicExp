package cn.gdgst.entity;

/**
 * Created by JenfeeMa on 2016/12/28.
 * All right reserved
 * email 1017033681@qq.com
 */

import java.io.Serializable;
import java.util.Map;

/**
 * 试卷中的题目类
 */
public class ExamTopic implements Serializable{
    private int id;
    /**
     * 试题的题目
     */
    private String title;

    /**
     * 试题的图片
     */
    private String img;

    /**
     * 试题的正确答案
     */
    private String right;

    /**
     * 选项A
     */
    private String optionA;

    /**
     * 选项B
     */
    private String optionB;

    /**
     * 选项C
     */
    private String optionC;

    /**
     * 选项D
     */
    private String optionD;

    /**
     * 该题的答案解析
     */
    private String analysis;

    private Map<Integer, String> map_Answer;

    public ExamTopic() {
    }

    public ExamTopic(int id, String title, String img, String right, String optionA, String optionB, String optionC, String optionD, String analysis, Map<Integer, String> map_Answer) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.right = right;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.analysis = analysis;
        this.map_Answer = map_Answer;
    }

    public int getId() {
        return this.id;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public Map<Integer, String> getMap_Answer() {
        return map_Answer;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getAnalysis() {
        return analysis;
    }
    public void setMap_Answer(Map<Integer, String> map_Answer) {
        this.map_Answer = map_Answer;
    }

    @Override
    public String toString() {
        return "ExamTopic{" +
                "title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", right='" + right + '\'' +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'' +
                ", optionC='" + optionC + '\'' +
                ", optionD='" + optionD + '\'' +
                '}';
    }
}
