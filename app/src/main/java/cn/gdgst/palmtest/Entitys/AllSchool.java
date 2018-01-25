package cn.gdgst.palmtest.Entitys;

/**
 * Created by Administrator on 12/20 0020.
 */

public class AllSchool {
/**
 *  "name":"东莞58中学",
 "pinyin":"dong58zhongxue"
 */

    private String name;
    private String pinyin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return "AllSchool{" +
                "name='" + name + '\'' +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }
}
