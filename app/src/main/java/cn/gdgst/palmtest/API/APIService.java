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
import cn.gdgst.palmtest.Entitys.AllSchool;
import cn.gdgst.palmtest.Entitys.AppSearchEntity;
import cn.gdgst.palmtest.Entitys.CollectEntity;
import cn.gdgst.palmtest.Entitys.HistoryEntity;
import cn.gdgst.palmtest.Entitys.PX_Cate_Entity;
import cn.gdgst.palmtest.Entitys.PatyDetail;
import cn.gdgst.palmtest.Entitys.UpdateInfo;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.Entitys.UserVote;
import cn.gdgst.palmtest.Entitys.VoteAction;
import cn.gdgst.palmtest.Entitys.WK_Detail_Entity;
import cn.gdgst.palmtest.Entitys.ZX_Detail_Entity;
import cn.gdgst.palmtest.bean.CategoryList_Entity;
import cn.gdgst.palmtest.bean.CollectionData;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.bean.UpdateInfo_Entity;
import cn.gdgst.palmtest.bean.VideoList_Entity;
import cn.gdgst.palmtest.bean.WenKuEntity;
import cn.gdgst.palmtest.bean.ZiXunEntity;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Retrofit 2.0中我们还可以在@Url里面定义完整的URL：这种情况下Base URL会被忽略。
 */
public interface APIService {
    //高中物理等分类
    @POST("category_list")
    //使用 RxJava 的方法,返回一个 Observable
    Observable<HttpResult<List<CategoryList_Entity>>> getCategoryList();

    //更新版本
    @POST("check_version")
    Observable<HttpResult<UpdateInfo_Entity>> getUpdateInfo(@QueryMap Map<String, String> options);

    //创客列表
    @POST("chuangke_list")
    Observable<HttpResult<List<ChuangKe>>> getChuangke_list(
                                                            @Query("desc_type") String desc_type,
                                                            @Query("category_id") String category_id,
                                                            @Query("page") String page);

    //文库
    @POST("wenku_list")
    Observable<HttpResult<List<WenKu>>> getWenKuList(@Query("desc_type") String desc_type,
                                                     @Query("category_id") String category_id,
                                                     @Query("page") String page);

    //文库详情
    @POST("wenku_detail")
    Observable<HttpResult<WK_Detail_Entity>> getWenKuDetail(@Query("id") String id);

    //article_list  会展中心134    资讯99 文章类   装备135  培训学院139 （视频类）
    //    装备135
    @POST("article_list")
    Observable<HttpResult<List<ZhuangBei>>> getArticleList(@Query("desc_type") String desc_type,
                                                           @Query("category_id") String category_id,
                                                           @Query("page") String page);

    /**
     * 获得实验装备的分类列表
     * @param id
     * @return
     */
    @POST("article_category_sub")
    Observable<HttpResult<List<PX_Cate_Entity>>> getZhuangBeiCarteray(@Query("id") String id);

    //    培训139
    @POST("article_list")
    Observable<HttpResult<List<PeiXun>>> getArticleListpx(@Query("desc_type") String desc_type,
                                                          @Query("category_id") String category_id,
                                                          @Query("page") String page);
    //   资讯99
    @POST("article_list")
    Observable<HttpResult<List<ZiXun>>> getArticleListzx(@Query("category_id") String category_id,
                                                         @Query("page") String page);
    //   会展中心134  http://shiyan360.cn/api/news?page=1（动态新闻）
    @POST("news")//article_list
    Observable<HttpResult<List<HuiZhan>>> getArticleListhz(                                     //@Query("category_id") String category_id,
                                                           @Query("page") String page);


    //article_detail    http://shiyan360.cn/api/news?id=1204(新闻动态详情页
    @POST("article_detail")
    Observable<HttpResult<ZX_Detail_Entity>> getArtDetail(@Query("id") String id);

    //同步视频  学科视频
    @POST("video_list")
    Observable<HttpResult<List<Video>>> getVideoList(@Query("desc_type") String desc_type,
                                                     @Query("category_id") String category_id,
                                                     @Query("gradeid") String grade_id,
                                                     @Query("page") String page);

    //名师视频
    @POST("mingshi_list")
    Observable<HttpResult<List<MingShi>>> getMingShiList(@Query("desc_type") String desc_type,
                                                         @Query("category_id") String category_id,
                                                         @Query("gradeid") String grade_id,
                                                         @Query("page") String page);

    //考试视频
    @POST("kaoshi_list")
    Observable<HttpResult<List<KaoShi>>> getKaoShiList(@Query("desc_type") String desc_type,
                                                       @Query("category_id") String category_id,
                                                       @Query("page") String page);

    /**
     * 仿真实验请求接口
     * @param desc_type
     * @param category_id
     * @param grade_id
     * @param page
     * @return
     */
    @POST("experiment_list")
    Observable<HttpResult<List<Experiment>>> geExperimentList(@Query("desc_type") String desc_type,
                                                              @Query("category_id") String category_id,
                                                              @Query("gradeid") String grade_id,
                                                              @Query("page") String page);

    //登陆
    @GET("user_login")
    Observable<HttpResult<UserEntity>> login(@Query("user_name") String user_name,
                                             @Query("user_pass") String user_pass,
                                             @Query("table") String table);

    //注册
    @POST("user_signup")
    Observable<HttpResult> register(@Query("user_name") String user_name,
                                    @Query("user_email") String user_code,
                                    @Query("user_pass") String user_pass);


    //发送验证码 code_type 1为注册 2为改密码
    //@FormUrlEncoded      //将会自动将请求参数的类型调整为application/x-www-form-urlencoded.但至少要有一个@filed注解
    @POST("check_code")
    Observable<HttpResult> getSendCode(@Query("user_name") String user_name,
                                       @Query("sms_code") String sms_code,
                                       @Query("code_type") String code_type);

    //改密码
    @POST("change_pass")
    Observable<HttpResult> getChangePass(@Query("accessToken") String accessToken,
                                         @Query("old_pass") String old_pass,
                                         @Query("new_pass") String new_pass);

    //重置密码
    @POST("find_pass")
    Observable<HttpResult> getResetPass(@Query("user_name") String userName,
                                        @Query("user_code") String userCode,
                                        @Query("user_pass") String userNewPass);

    //修改个人资料
    @POST("user_profile_update")
    Observable<HttpResult> getChangeProfile(@Query("accessToken") String accessToken,
                                            @Query("nickname") String nickname,
                                            @Query("school") String school,
                                            @Query("name") String name);

    //反馈
    @POST("guestbook")
    Observable<HttpResult> getGuestbook(@Query("content") String content,
                                        @Query("name") String name,
                                        @Query("tel") String tel,
                                        @Query("email") String email,
                                        @Query("qq") String qq);

    //收藏
    @POST("user_collect")
    Observable<HttpResult<List<CollectEntity>>> getCollect(@Query("accessToken") String accessToken,
                                                           //@Query("page") String page,
                                                           @Query("model") String model);

    //删除收藏
    @POST("user_collect_delete")
    Observable<HttpResult> getDeleteCollection(@Query("accessToken") String accessToken,
                                         @Query("id") String id,
                                         @Query("model") String model);

    //增加收藏
    @POST("user_collect_add")
    Observable<HttpResult<List<CollectionData>>> getAddCollection(@Query("accessToken") String accessToken,
                                                                  @Query("model") String model,
                                                                  @Query("id") String id);

    //浏览记录
    @POST("user_history")
    Observable<HttpResult<List<HistoryEntity>>> getHistoryList(@Query("accessToken") String accessToken,
                                                               @Query("page") String page);

    //添加浏览记录
    @POST("user_history_add")
    Observable<HttpResult> getAddHistoryList(@Query("accessToken") String accessToken,
                                             @Query("id") String id,
                                             @Query("model") String model);

    //清空浏览记录
    @POST("user_history_clear")
    Observable<HttpResult> getClearHistoryList(@Query("accessToken") String accessToken);

    //获取试卷列表
    @POST("examinPaperList")
    Observable<HttpResult<List<ExamPaper>>> examinPaperList(@Query("page") int page,
                                                           // @Query("cid") int cid,
                                                            @Query("school") String school,
                                                            @Query("banji") String banji,
                                                            @Query("nj") String nj,
                                                            @Query("id") String id);

    @POST("examinPagerList")
    Observable<HttpResult<List<ExamPaper>>> examinPaperList(@Query("page") int page, @Query("cid") int cid);



    //获取指定详细Id的试卷内容
   // @POST("examinPaperDetail")
    //Observable<HttpResult<Map<Integer,ExamTopic>>> examinPaperDetail(@Query("id") int id);

    //提交试卷
    @POST("submit_Paper")
    Observable<HttpResult> submit_Paper(@Query("result") Map<Integer,String> map_result, @Query("username") String username, @Query("studentId") String studentId);

    //@Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("submitPaperJson")
    Observable<HttpResult> submitPaperJson(@Query("json") String jsonData);

    /**
     * 从服务器下载头像
     * @param fileUrl
     * @return
     */
    @GET
    Observable<ResponseBody> downloadAvatarFromNet(@Url String fileUrl);

    /**
     * 把头像上传到服务器
     * @param requestBody 用@Part("attribute") RequestBody 直接来定义请求中的字符串字段
     * @param part 需要注意用@Part MultipartBody.Part 注解来定义我们要上传的图片文件
     * @return
     */
    @Multipart
    @POST("http://www.shiyan360.cn/index.php/api/uploadAvatar")
    Call<ResponseBody> uploadAvatarToNet(@Part("attribute") RequestBody requestBody, @Part MultipartBody.Part part);

    /**
     * 上传仿真视频的录制视频到服务器上
     * @param requestBody
     * @param part
     * @return
     */
    @Multipart
    @POST("http://www.shiyan360.cn/index.php/api/.....")
    Observable<ResponseBody> uploadScreemVedioToNet(@Part("...") RequestBody requestBody, @Part MultipartBody.Part part);

    /**
     * APP搜索
     * @param keyword 要搜索的关键字
     * @param p 分页
     * @return
     */
    @POST("appsearch")
    Observable<HttpResult<List<AppSearchEntity>>> appSearch(@Query("keyword") String keyword,
                                                            @Query("p") int p);

    /**
     * 检查版本信息
     * @param version_code
     * @return
     */
    @POST("check_version")
    Observable<HttpResult<UpdateInfo>> updataInfoRemark(@Query("version_code") String version_code);

    /**
     * 获取所有学校的名称和数据库表名
     * @return
     */
    //http://shiyan360.cn/api/getAllSchool
    @GET("getAllSchool")
    Observable<HttpResult<List<AllSchool>>> getAllSchool();

    /**
     * 获取投票选手信息
     * @param page
     * @return
     */
    //http://shiyan360.cn/api/vote_video?page=1
    @GET("vote_video")
    Observable<HttpResult<List<UserVote>>> getUserVote(@Query("page") int page);

    /**
     * 获取活动内容详情
     * @return
     */
    //http://shiyan360.cn/api/activity
    @GET("activity")
    Observable<PatyDetail> getPatyDetail();

    /**
     * 投票接口，传一个id，返回当前总票数
     * @return
     */
    //http://shiyan360.cn/api/vote_setinc?id=3975
    @GET("vote_setinc")
    Observable<HttpResult<VoteAction>> getVoteActionResult(@Query("id") String _id);

    /**
     * 搜索接口，传一个关键，返回搜索结果。数据库找不到返回一个错误,默认第1页
     * @param keyword
     * @param page
     * @return
     */
    //http://shiyan360.cn/api/vote_search?keyword=66&page=1
    @GET("vote_search")
    Observable<HttpResult<List<UserVote>>> getSearchUserVote(@Query("keyword") String keyword,
                                                         @Query("page") int page);

    //http://shiyan360.cn/api/result_desc?page=1
    @GET("result_desc")
    Observable<HttpResult<List<UserVote>>> getSortResult(@Query("page") int page);


/*    @Multipart
    @POST("AppYuFaKu/uploadHeadImg")
    Observable<baseresultentity<uploadresulte>> uploadImage(@Part("uid") RequestBody uid,
                                                            @Part("auth_key") RequestBody  auth_key,
                                                            @Part MultipartBody.Part file);*/





}
