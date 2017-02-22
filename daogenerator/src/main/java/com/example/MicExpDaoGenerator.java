package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by Don on 2016/7/19.
 */
public class MicExpDaoGenerator {

    public static final int version = 3;//数据库版本号
    public static final String entityPackageName = "com.micexp.entity";//实体生存的包名
    public static final String daoPackageName = "com.micexp.dao";//指定dao层模板的包

    //自动生成模板类存放的绝对地址，也就是你的module创建的session文件夹 也就是java-gen
    public static final String autoGenerateJavaPath = "C:\\Users\\Don\\Desktop\\MicExp\\app\\src\\main\\java-gen";

    public static void main(String[] args) throws Exception {

        Schema schema = new Schema(version, entityPackageName);
        //添加节点
        addChuangke(schema);
        addZiXun(schema);
        addWenKu(schema);
        addMingShi(schema);
        addPeiXun(schema);
        addKaoShi(schema);
        addHuiZhan(schema);
        addZhuangBei(schema);
        addVideo(schema);
        addscience(schema);
        addmidPhy(schema);
        addmidBio(schema);
        addmidChe(schema);
        addexp(schema);

        schema.setDefaultJavaPackageDao(daoPackageName);// 设置数据的会话层  //如果不指定 默认与entityPackageName一致


        //将生成的内容放在指定的路径下C:\Users\admin\Desktop\shujuku\MyApplication\app\src\main\java-gen
        try {
            new DaoGenerator().generateAll(schema, autoGenerateJavaPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addChuangke(Schema schema) {
        Entity entity = schema.addEntity("ChuangKe");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("ChuangKeDao");//设置dao类的名称
        entity.setTableName("chuangke_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addZiXun(Schema schema) {
        Entity entity = schema.addEntity("ZiXun");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("pid");
        entity.addStringProperty("title");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("content");
        entity.addStringProperty("time");
        entity.setClassNameDao("ZiXunDao");//设置dao类的名称
        entity.setTableName("zixun_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addWenKu(Schema schema) {
        Entity entity = schema.addEntity("WenKu");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("title");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("file_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("WenKuDao");//设置dao类的名称
        entity.setTableName("wenku_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addMingShi(Schema schema) {
        Entity entity = schema.addEntity("MingShi");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("gradeid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("MingShiDao");//设置dao类的名称
        entity.setTableName("mingshi_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addPeiXun(Schema schema) {
        Entity entity = schema.addEntity("PeiXun");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("pid");
        entity.addStringProperty("title");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("content");
        entity.addStringProperty("time");
        entity.setClassNameDao("PeiXunDao");//设置dao类的名称
        entity.setTableName("peixun_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addKaoShi(Schema schema) {
        Entity entity = schema.addEntity("KaoShi");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("KaoShiDao");//设置dao类的名称
        entity.setTableName("kaoshi_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addHuiZhan(Schema schema) {
        Entity entity = schema.addEntity("HuiZhan");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("pid");
        entity.addStringProperty("title");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("content");
        entity.addStringProperty("time");
        entity.setClassNameDao("HuiZhanDao");//设置dao类的名称
        entity.setTableName("huizhan_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addZhuangBei(Schema schema) {
        Entity entity = schema.addEntity("ZhuangBei");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("pid");
        entity.addStringProperty("title");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("content");
        entity.addStringProperty("time");
        entity.setClassNameDao("ZhuangBeiDao");//设置dao类的名称
        entity.setTableName("zhuangbei_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addVideo(Schema schema) {
        Entity entity = schema.addEntity("Video");
        entity.addIdProperty();//主键
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("gradeid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("VideoDao");//设置dao类的名称
        entity.setTableName("video_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addscience(Schema schema) {
        Entity entity = schema.addEntity("Science");
        entity.addIdProperty();
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("gradeid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("ScienceDao");//设置dao类的名称
        entity.setTableName("science_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addmidPhy(Schema schema) {
        Entity entity = schema.addEntity("MidPhy");
        entity.addIdProperty();
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("gradeid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("MidPhyDao");//设置dao类的名称
        entity.setTableName("midphy_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addmidChe(Schema schema) {
        Entity entity = schema.addEntity("MidChe");
        entity.addIdProperty();
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("gradeid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("MidCheDao");//设置dao类的名称
        entity.setTableName("midche_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }

    private static void addmidBio(Schema schema) {
        Entity entity = schema.addEntity("MidBio");
        entity.addIdProperty();
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("gradeid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("video_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("MidBioDao");//设置dao类的名称
        entity.setTableName("midbio_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }
    private static void addexp(Schema schema) {
        Entity entity = schema.addEntity("Experiment");
        entity.addIdProperty();
        entity.addStringProperty("gid");
        entity.addStringProperty("cateid");
        entity.addStringProperty("gradeid");
        entity.addStringProperty("name");
        entity.addStringProperty("img_url");
        entity.addStringProperty("img_url_s");
        entity.addStringProperty("html5_url");
        entity.addStringProperty("time");
        entity.setClassNameDao("ExperimentDao");//设置dao类的名称
        entity.setTableName("exp_list");//设置表名,默认是entityClassName(NOTE)的大写形式
    }
}
