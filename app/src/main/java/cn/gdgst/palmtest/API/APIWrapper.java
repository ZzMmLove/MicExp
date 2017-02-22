package cn.gdgst.palmtest.API;


import cn.gdgst.entity.ChuangKe;
import cn.gdgst.entity.ExamPaper;
import cn.gdgst.entity.Experiment;
import cn.gdgst.entity.HuiZhan;
import cn.gdgst.entity.KaoShi;
import cn.gdgst.entity.MingShi;
import cn.gdgst.entity.PeiXun;
import cn.gdgst.entity.Video;
import cn.gdgst.entity.WenKu;
import cn.gdgst.entity.ZhuangBei;
import cn.gdgst.entity.ZiXun;
import cn.gdgst.palmtest.BuildConfig;
import cn.gdgst.palmtest.API.util.RetrofitUtil;
import cn.gdgst.palmtest.Entitys.AppSearchEntity;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.bean.ApiException;
import cn.gdgst.palmtest.bean.CategoryList_Entity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.bean.UpdateInfo_Entity;
import cn.gdgst.palmtest.bean.VideoList_Entity;
import cn.gdgst.palmtest.bean.WenKuEntity;
import cn.gdgst.palmtest.bean.ZiXunEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class APIWrapper extends RetrofitUtil {

    private static APIWrapper mAPIWrapper;

    public APIWrapper() {
    }

    public static APIWrapper getInstance() {
        if (mAPIWrapper == null) {
            mAPIWrapper = new APIWrapper();
        }
        return mAPIWrapper;
    }

    //获取高中物理等分类
    public Observable<HttpResult<List<CategoryList_Entity>>> getCate() {
        Observable<HttpResult<List<CategoryList_Entity>>> observable = getAPIService().getCategoryList();
        return observable;
    }

    //获取更新信息
    public Observable<HttpResult<UpdateInfo_Entity>> getUpdateInfo() {
        Map<String, String> options = new HashMap<String, String>();
        options.put("version_code", BuildConfig.VERSION_NAME);
        Observable<HttpResult<UpdateInfo_Entity>> observable = getAPIService().getUpdateInfo(options);
        return observable;
    }

    //获取创客视频列表
    public Observable<HttpResult<List<ChuangKe>>> getChuangke_list(String desc_type, String category_id, String page) {
        Observable<HttpResult<List<ChuangKe>>> observable = getAPIService().getChuangke_list(desc_type, category_id, page);
        return observable;
    }

    //文库
    public Observable<HttpResult<List<WenKu>>> getWenKuList(String desc_type, String category_id, String page) {
        Observable<HttpResult<List<WenKu>>> observable = getAPIService().getWenKuList(desc_type, category_id, page);
        return observable;
    }

    //文库详情
    public Observable<HttpResult<WenKuEntity>> getWenKuDetail(String id) {
        Observable<HttpResult<WenKuEntity>> observable = getAPIService().getWenKuDetail(id);
        return observable;
    }

    //article_list   会展中心134    资讯99 文章类   装备135 培训学院139 （视频类）
//    装备135
    public Observable<HttpResult<List<ZhuangBei>>> getArticleList(String desc_type, String category_id, String page) {
        Observable<HttpResult<List<ZhuangBei>>> observable = getAPIService().getArticleList(desc_type, category_id, page);
        return observable;
    }
//    培训学院139
    public Observable<HttpResult<List<PeiXun>>> getArticleListpx(String desc_type, String category_id, String page) {
        Observable<HttpResult<List<PeiXun>>> observable = getAPIService().getArticleListpx(desc_type, category_id, page);
        return observable;
    }
//    资讯99
    public Observable<HttpResult<List<ZiXun>>> getArticleListzx(String category_id, String page) {
        Observable<HttpResult<List<ZiXun>>> observable = getAPIService().getArticleListzx(category_id, page);
        return observable;
    }
    //    会展中心134
    public Observable<HttpResult<List<HuiZhan>>> getArticleListhz(String category_id, String page) {
        Observable<HttpResult<List<HuiZhan>>> observable = getAPIService().getArticleListhz(category_id, page);
        return observable;
    }

    //article_detail  会展中心134    资讯99 文章类
    public Observable<HttpResult<ZiXunEntity>> getArtDetail(String id) {
        Observable<HttpResult<ZiXunEntity>> observable = getAPIService().getArtDetail(id);
        return observable;
    }

    //同步视频  学科视频
    public Observable<HttpResult<List<Video>>> getVideoList(String desc_type, String category_id, String grade_id, String page) {
        Observable<HttpResult<List<Video>>> observable = getAPIService().getVideoList(desc_type, category_id, grade_id, page);
        return observable;
    }

    //名师视频
    public Observable<HttpResult<List<MingShi>>> getMingShiList(String desc_type, String category_id, String grade_id, String page) {
        Observable<HttpResult<List<MingShi>>> observable = getAPIService().getMingShiList(desc_type, category_id, grade_id, page);
        return observable;
    }
    //考试视频
    public Observable<HttpResult<List<KaoShi>>> getKaoShiList(String desc_type, String category_id, String page) {
        Observable<HttpResult<List<KaoShi>>> observable = getAPIService().getKaoShiList(desc_type, category_id, page);
        return observable;
    }
    //实验
    public Observable<HttpResult<List<Experiment>>> geExperimentList(String desc_type, String category_id, String grade_id, String page) {
        Observable<HttpResult<List<Experiment>>> observable = getAPIService().geExperimentList(desc_type, category_id, grade_id, page);
        return observable;
    }

    /**
     * 登录 // FIXME: 2017/2/9 JenfeeMa
     * @param user_name
     * @param user_pass
     * @return
     */
    public Observable<HttpResult<UserEntity>> login(String user_name, String user_pass) {
        Observable<HttpResult<UserEntity>> observable = getAPIService().login(user_name, user_pass);
        return observable;
    }

    /**
     * 注册  未测试
     * @param user_name
     * @param user_code
     * @param user_pass
     * @return
     */
    public Observable<HttpResult> register(String user_name, String user_code,String user_pass) {
        Observable<HttpResult> observable = getAPIService().register(user_name,user_code, user_pass);
        return observable;
    }
    //发送验证码 code_type 1为注册 2为改密码  未测试
    public Observable<HttpResult> getSendCode(String user_name, String sms_code,String code_type) {
        Observable<HttpResult> observable = getAPIService().getSendCode(user_name,sms_code, code_type);
        return observable;
    }
    //改密码
    public Observable<HttpResult> getChangePass(String accessToken, String old_pass,String new_pass) {
        Observable<HttpResult> observable = getAPIService().getChangePass(accessToken,old_pass, new_pass);
        return observable;
    }
    //修改个人资料  未测试
    public Observable<HttpResult> getChangeProfile(String accessToken, String nickname,String school,String name) {
        Observable<HttpResult> observable = getAPIService().getChangeProfile(accessToken,nickname, school,name);
        return observable;
    }
    //反馈  未测试
    public Observable<HttpResult> getGuestbook(String content, String name,String tel,String email,String qq) {
        Observable<HttpResult> observable = getAPIService().getGuestbook(content,name, tel,email,qq);
        return observable;
    }
    //收藏
    public Observable<HttpResult<List<VideoList_Entity>>> getCollect(String accessToken, String page, String model) {
        Observable<HttpResult<List<VideoList_Entity>>> observable = getAPIService().getCollect(accessToken,page, model);
        return observable;
    }
    //删除收藏
    public Observable<HttpResult> getDeleteInfo(String accessToken,String id,String model) {
        Observable<HttpResult> observable = getAPIService().getDeleteInfo(accessToken,id,model);
        return observable;
    }
    //增加收藏
    public Observable<HttpResult> getAddInfo(String accessToken,String id,String model) {
        Observable<HttpResult> observable = getAPIService().getAddInfo(accessToken,id,model);
        return observable;
    }
    //浏览记录
    public Observable<HttpResult<List<VideoList_Entity>>> getHistoryList(String accessToken, String page) {
        Observable<HttpResult<List<VideoList_Entity>>> observable = getAPIService().getHistoryList(accessToken,page);
        return observable;
    }
    //添加浏览记录
    public Observable<HttpResult> getAddHistoryList(String accessToken,String id,String model) {
        Observable<HttpResult> observable = getAPIService().getAddHistoryList(accessToken,id,model);
        return observable;
    }
    //清空浏览记录
    public Observable<HttpResult> getClearHistoryList(String accessToken) {
        Observable<HttpResult> observable = getAPIService().getClearHistoryList(accessToken);
        return observable;
    }

    public Observable<HttpResult<List<ExamPaper>>> examinPaperList (int page, int cid) {
        Observable<HttpResult<List<ExamPaper>>> observable = getAPIService().examinPaperList(page, cid);
        return observable;
    }

    public Observable<HttpResult> submitPaper(Map<Integer, String> map_result, String username, String studentId) {
        Observable<HttpResult> observable = getAPIService().submit_Paper(map_result, username, studentId);
        return observable;
    }

    //public Observable<HttpResult<Map<Integer,ExamTopic>>> examinPaperDetail (int id) {
    //    Observable<HttpResult<Map<Integer,ExamTopic>>> observable = getAPIService().examinPaperDetail(id);
    //    return  observable;
    //}

    public Observable<HttpResult> submitPaperJson(String toJSON) {
        //RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), toJSON);
        Observable<HttpResult> observable = getAPIService().submitPaperJson(toJSON);
        return observable;
    }

    /**
     * 从服务器下载头像图片
     * @param url
     * @return
     */
    public Observable<ResponseBody> downloadAvatarFromNet(String url) {
        return getAPIService().downloadAvatarFromNet(url);
    }

    /**
     * 上次头像图片到服务器
     * @param requestBody
     * @param part
     * @return
     */
    public Call<ResponseBody> uploadAvatarToNet(RequestBody requestBody, MultipartBody.Part part) {
        return getAPIService().uploadAvatarToNet(requestBody, part);
    }

    public Observable<HttpResult<List<AppSearchEntity>>> appSearch(String keyword, int p) {
        return getAPIService().appSearch(keyword, p);
    }

    public <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class ResponseFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> response) {
            if (response.getError_code() == 0) {
                throw new ApiException(100);
            }
            return response.getData();
        }
    }
}
