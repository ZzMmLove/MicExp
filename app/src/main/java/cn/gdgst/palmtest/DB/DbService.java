package cn.gdgst.palmtest.DB;

/**
 * Created by Don on 2016/7/20.
 */

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import cn.gdgst.dao.ChuangKeDao;
import cn.gdgst.dao.DaoSession;
import cn.gdgst.dao.ExperimentDao;
import cn.gdgst.dao.HuiZhanDao;
import cn.gdgst.dao.KaoShiDao;
import cn.gdgst.dao.MingShiDao;
import cn.gdgst.dao.PeiXunDao;
import cn.gdgst.dao.VideoDao;
import cn.gdgst.dao.WenKuDao;
import cn.gdgst.dao.ZhuangBeiDao;
import cn.gdgst.dao.ZiXunDao;
import cn.gdgst.entity.ChuangKe;
import cn.gdgst.entity.Experiment;
import cn.gdgst.entity.HuiZhan;
import cn.gdgst.entity.KaoShi;
import cn.gdgst.entity.MingShi;
import cn.gdgst.entity.PeiXun;
import cn.gdgst.entity.Video;
import cn.gdgst.entity.WenKu;
import cn.gdgst.entity.ZhuangBei;
import cn.gdgst.entity.ZiXun;
import cn.gdgst.palmtest.base.BaseApplication;

import java.util.List;

/**
 * 用户操作类
 * Created by cg on 2015/12/29.
 */
public class DbService {
    private static final String TAG = DbService.class.getSimpleName();
    private static DbService instance;
    private static Context appContext;
    private DaoSession mDaoSession;
    private ChuangKeDao chuangKeDao;

    private MingShiDao mingShiDao;
    private ZhuangBeiDao zhuangBeiDao;
    private KaoShiDao kaoShiDao;
    private PeiXunDao peiXunDao;
    private ZiXunDao ziXunDao;
    private HuiZhanDao huiZhanDao;
    private WenKuDao wenKuDao;
    private VideoDao videoDao;
    private ExperimentDao experimentDao;

    private DbService() {
    }

    /**
     * 采用单例模式
     *
     * @param context 上下文
     * @return dbservice
     */
    public static DbService getInstance(Context context) {
        if (instance == null) {
                instance = new DbService();
                if (appContext == null) {
                    appContext = context.getApplicationContext();
                }
            instance.mDaoSession = BaseApplication.getDaoSession(context);
            instance.chuangKeDao = instance.mDaoSession.getChuangKeDao();

            instance.mingShiDao = instance.mDaoSession.getMingShiDao();
            instance.zhuangBeiDao = instance.mDaoSession.getZhuangBeiDao();
            instance.kaoShiDao = instance.mDaoSession.getKaoShiDao();
            instance.peiXunDao = instance.mDaoSession.getPeiXunDao();
            instance.ziXunDao = instance.mDaoSession.getZiXunDao();
            instance.huiZhanDao = instance.mDaoSession.getHuiZhanDao();
            instance.wenKuDao = instance.mDaoSession.getWenKuDao();
            instance.videoDao = instance.mDaoSession.getVideoDao();
            instance.experimentDao=instance.mDaoSession.getExperimentDao();
        }
        return instance;
    }

    /***************************创客视频*************************/

    /**
     * 根据信息,插入或修改信息
     *
     * @param chuangKe 信息
     * @return 插件或修改的用户id
     */
    public long saveChuangKeInfo(ChuangKe chuangKe) {
        return chuangKeDao.insertOrReplace(chuangKe);
    }


    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveChuangKeLists(final List<ChuangKe> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        chuangKeDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    ChuangKe user = list.get(i);
                    chuangKeDao.insertOrReplace(user);
                }
            }
        });

    }

    /**
     * 根据id,取出创客视频列表的信息
     *
     * @param id id
     * @return 创客视频列表的信息信息
     */
    public ChuangKe loadChuangKeById(long id) {
        if (!TextUtils.isEmpty(id + "")) {
            return chuangKeDao.load(id);
        }
        return null;
    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<ChuangKe> loadAllChuangKe() {
        return chuangKeDao.loadAll();
    }

    /**
     * 生成按id倒排序的列表
     *
     * @return 倒排数据
     */
    public List<ChuangKe> loadAllChuangKeByOrder() {
        return chuangKeDao.queryBuilder().orderDesc(ChuangKeDao.Properties.Id).list();
    }

    /**
     * 根据查询条件,返回数据列表
     *
     * @param where  条件
     * @param params 参数
     * @return 数据列表
     */
    public List<ChuangKe> queryChuangKe(String where, String... params) {
        return chuangKeDao.queryRaw(where, params);
    }

    /**
     * 删除所有数据
     */
    public void deleteAllChuangKe() {
        chuangKeDao.deleteAll();
    }

    /**
     * 根据id,删除数据
     *
     * @param id 用户id
     */
    public void deleteChuangKe(long id) {
        chuangKeDao.deleteByKey(id);
        Log.i(TAG, "delete");
    }

    /**
     * 根据用户类,删除信息
     *
     * @param chuangKe 信息类
     */
    public void deleteChuangKe(ChuangKe chuangKe) {
        chuangKeDao.delete(chuangKe);
    }

/***************************名师视频*************************/
    /**
     * 根据信息,插入或修改信息
     *
     * @param mingShi 信息
     * @return 插件或修改的用户id
     */
    public long saveMingShiInfo(MingShi mingShi) {
        return mingShiDao.insertOrReplace(mingShi);
    }


    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveMingShiLists(final List<MingShi> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        mingShiDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    MingShi user = list.get(i);
                    mingShiDao.insertOrReplace(user);
                }
            }
        });

    }

    /**
     * 根据id,取出创客视频列表的信息
     *
     * @param id id
     * @return 创客视频列表的信息信息
     */
    public MingShi loadMingShiById(long id) {
        if (!TextUtils.isEmpty(id + "")) {
            return mingShiDao.load(id);
        }
        return null;
    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<MingShi> loadAllMingShi() {
        return mingShiDao.loadAll();
    }

    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<MingShi> loadAllMingShiByOrder() {
        return mingShiDao.queryBuilder().orderDesc(MingShiDao.Properties.Id).list();
    }

    /**
     * 根据查询条件,返回数据列表
     *
     * @param where  条件
     * @param params 参数
     * @return 数据列表
     */
    public List<MingShi> queryMingShi(String where, String... params) {
        return mingShiDao.queryRaw(where, params);
    }

    /**
     * 删除所有数据
     */
    public void deleteAllMingShi() {
        mingShiDao.deleteAll();
    }

    /**
     * 根据id,删除数据
     *
     * @param id 用户id
     */
    public void deleteMingShi(long id) {
        mingShiDao.deleteByKey(id);
        Log.i(TAG, "delete");
    }

    /**
     * 根据用户类,删除信息
     *
     * @param mingShi 信息类
     */
    public void deleteMingShi(MingShi mingShi) {
        mingShiDao.delete(mingShi);
    }

    /***************************实验装备*************************/

    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveZhuangBeiLists(final List<ZhuangBei> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        zhuangBeiDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    ZhuangBei user = list.get(i);
                    zhuangBeiDao.insertOrReplace(user);
                }
            }
        });

    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<ZhuangBei> loadAllZhuangBei() {
        return zhuangBeiDao.loadAll();
    }

    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<ZhuangBei> loadAllZhuangBeiByOrder() {
        return zhuangBeiDao.queryBuilder().orderDesc(ZhuangBeiDao.Properties.Id).list();
    }

    /***************************考试视频*************************/

    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveKaoShiLists(final List<KaoShi> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        kaoShiDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    KaoShi user = list.get(i);
                    kaoShiDao.insertOrReplace(user);
                }
            }
        });

    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<KaoShi> loadAllKaoShi() {
        return kaoShiDao.loadAll();
    }

    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<KaoShi> loadAllKaoShiByOrder() {
        return kaoShiDao.queryBuilder().orderDesc(KaoShiDao.Properties.Id).list();
    }
    /***************************培训视频*************************/

    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void savePeiXunLists(final List<PeiXun> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        peiXunDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    PeiXun user = list.get(i);
                    peiXunDao.insertOrReplace(user);
                }
            }
        });

    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<PeiXun> loadAllPeiXun() {
        return peiXunDao.loadAll();
    }
    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<PeiXun> loadAllPeiXunByOrder() {
        return peiXunDao.queryBuilder().orderDesc(PeiXunDao.Properties.Id).list();
    }
    /***************************资讯*************************/
    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveZiXunLists(final List<ZiXun> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        peiXunDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    ZiXun user = list.get(i);
                    ziXunDao.insertOrReplace(user);
                }
            }
        });

    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<ZiXun> loadAllZiXun() {
        return ziXunDao.loadAll();
    }
    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<ZiXun> loadAllZiXunByOrder() {
        return ziXunDao.queryBuilder().orderDesc(ZiXunDao.Properties.Id).list();
    }
    /***************************会展*************************/
    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveHuiZhanLists(final List<HuiZhan> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        huiZhanDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    HuiZhan user = list.get(i);
                    huiZhanDao.insertOrReplace(user);
                }
            }
        });

    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<HuiZhan> loadAllHuiZhan() {
        return huiZhanDao.loadAll();
    }
    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<HuiZhan> loadAllHuiZhanByOrder() {
        return huiZhanDao.queryBuilder().orderDesc(HuiZhanDao.Properties.Id).list();
    }
    /***************************文库*************************/
    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveWenKuLists(final List<WenKu> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        wenKuDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    WenKu user = list.get(i);
                    wenKuDao.insertOrReplace(user);
                }
            }
        });
    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<WenKu> loadAllWenKu() {
        return wenKuDao.loadAll();
    }
    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<WenKu> loadAllWenKuByOrder() {
        return wenKuDao.queryBuilder().orderDesc(WenKuDao.Properties.Id).list();
    }

    /***************************视频*************************/
    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveVideoLists(final List<Video> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        videoDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    Video user = list.get(i);
                    videoDao.insertOrReplace(user);
                }
            }
        });
    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<Video> loadAllVideo() {
        return videoDao.loadAll();
    }
    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<Video> loadAllVideoyOrder() {
        return videoDao.queryBuilder().orderDesc(VideoDao.Properties.Id).list();
    }

    /***************************实验*************************/
    /**
     * 批量插入或修改信息
     *
     * @param list 信息列表
     */
    public void saveExpLists(final List<Experiment> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        experimentDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    Experiment user = list.get(i);
                    experimentDao.insertOrReplace(user);
                }
            }
        });
    }

    /**
     * 取出所有数据
     *
     * @return 所有数据信息
     */
    public List<Experiment> loadAllExp() {
        return experimentDao.loadAll();
    }
    /**
     * 生成按id倒排序的列表
     * @return 倒排数据
     */
    public List<Experiment> loadAllExpOrder() {
        return experimentDao.queryBuilder().orderDesc(ExperimentDao.Properties.Id).list();
    }
}