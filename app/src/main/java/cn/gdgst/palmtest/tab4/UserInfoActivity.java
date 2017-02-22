package cn.gdgst.palmtest.tab4;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.utils.ImageUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserInfoActivity extends AppCompatActivity implements OnClickListener {
    private ImageView iv_avatar;
    private TextView txt_phonenum, tv_changepass, tv_nickname, tv_name, tv_sex, tv_identity;
    private String phonenum;
    private Button btn_loginout;
    private String accessToken;
    private RelativeLayout rl_avatar, rl_nickname, rl_name, rl_sex, rl_identity, rl_school, rl_phone, rl_pass;
    private SVProgressHUD progressDialog;

    Dialog dialog;
    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    // private static final String IMGURL =
    // Environment.getExternalStorageDirectory().getPath()+"/Android/data/com.micexp.sdk";
    private static final String IMGURL = Environment.getExternalStorageDirectory() + "/";
    /* 照相机缓存头像名称 */
    private static final String IMAGE_FILE_NAME_TEMP = "temp_faceImage.jpg";
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";

    /**
     * 初始化sharedPreferences
     */
    private SharedPreferences sp = null;
    private String sexx = null;
    private String Identity = null;
    private String avatar_file_name;
    private boolean isExceptionForSDCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        progressDialog = new SVProgressHUD(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("用户信息");
        InitView();
    }

    private void InitView() {
        txt_phonenum = (TextView) findViewById(R.id.txt_phonenum);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_identity = (TextView) findViewById(R.id.tv_identity);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        btn_loginout = (Button) findViewById(R.id.btn_loginout);
        rl_avatar = (RelativeLayout) findViewById(R.id.rl_avatar);
        rl_avatar.setOnClickListener(this);
        rl_nickname = (RelativeLayout) findViewById(R.id.rl_nikname);
        rl_nickname.setOnClickListener(this);
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);
        rl_name.setOnClickListener(this);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_sex.setOnClickListener(this);
        rl_identity = (RelativeLayout) findViewById(R.id.rl_identity);
        rl_identity.setOnClickListener(this);
        rl_school = (RelativeLayout) findViewById(R.id.rl_school);
        rl_school.setOnClickListener(this);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        rl_phone.setOnClickListener(this);
        rl_pass = (RelativeLayout) findViewById(R.id.rl_pass);
        rl_pass.setOnClickListener(this);
        btn_loginout.setOnClickListener(this);

        sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");

		/* 将头像转为圆形 */
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.default_avatar);
        iv_avatar.setImageBitmap(bmp);

        Bundle bundle;
        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            UserEntity user = (UserEntity) bundle.getSerializable("user_info");
            Log.v("UserInfoActivity", "从服务器获取的头像的图片的路径:"+user.getAvatar());
            //利用Glide图片加载框架加载用户头像图片
            Glide.with(this).load(user.getAvatar()).into(iv_avatar);

            //http://www.shiyan360.cn/Public/Uploads/avatar/597.jpg
            //(第一种方式获取URL最后的文件名)String string_valid = user.getAvatar().substring(46);
            //(第二种方式获取URL最后的文件名)
            /*avatar_file_name = user.getAvatar().substring(user.getAvatar().lastIndexOf("/")+1);
            File file = isExistsNativeFile(avatar_file_name);
            //File file = new File(getExternalFilesDir(null)+"/syzst_avatar.png");
            if (file.exists()) {
                Log.v("UserInfoActivity", "打印本地头像的文件绝对路径:"+file.getAbsolutePath());
                //如果本地的头像文件名和服务器的头像文件名相同则显示
                if (file.getName().equals(avatar_file_name)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    iv_avatar.setImageBitmap(ImageUtil.toRoundBitmap(bitmap));
                }else {
                    //否则从服务器中下载新的头像名,并显示
                    downloadAvatarFromNet(user.getAvatar());
                }
            }else {
                downloadAvatarFromNet(user.getAvatar());
            }*/

            if (!user.getNickname().isEmpty()) {
                tv_nickname.setText(user.getNickname());
            } else {
                tv_nickname.setText("未填写");
            }
            if (!user.getName().isEmpty()) {
                tv_name.setText(user.getName());
            } else {
                tv_name.setText("未填写");
            }

            if (user.getSex() == 0) {
                tv_sex.setText("男");
            } else {
                tv_sex.setText("女");
            }

            tv_identity.setText("学生");

            phonenum = bundle.getString("username");
            txt_phonenum.setText(phonenum);
        }
    }

    /**
     * 此方法用于创建本地内存卡中的PalmTest中的头像文件
     * @param avatar_name
     * @return
     */
    private File isExistsNativeFile(String avatar_name) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            File file_ExternalStorageDir = Environment.getExternalStorageDirectory();
            File file_PalmTest = new File(file_ExternalStorageDir.toString() + "/PalmTest");
            boolean is_mkdir = file_PalmTest.mkdir();
            boolean is_Directory = file_PalmTest.isDirectory();
            file_PalmTest.setReadable(true);
            file_PalmTest.setWritable(true);
            File file_syzst_avatar = new File(file_PalmTest.getAbsolutePath()+"/"+avatar_name);
            return file_syzst_avatar;
        }
        return null;
    }

    /**
     * 此方法用于从服务器下载用户的头像
     * @param avatar_url
     */
    private void downloadAvatarFromNet(final String avatar_url) {
        APIWrapper.getInstance().downloadAvatarFromNet(avatar_url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        if (writeResponseBodyToDisk(responseBody)) {
                            Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                            iv_avatar.setImageBitmap(ImageUtil.toRoundBitmap(bitmap));
                        }

                    }
                });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File newAvatarFile = new File(Environment.getExternalStorageDirectory()+"/PalmTest/"+avatar_file_name);
            if (newAvatarFile.exists()) {
                boolean isDelete = newAvatarFile.delete();
                if (!isDelete) {
                    return false;
                }
            }
            Log.v("UserInfoActivity", "头像的图片的绝对路径:"+newAvatarFile.getAbsolutePath());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(newAvatarFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("UserInfoActivity", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loginout:
                Editor editor = sp.edit();// 获取编辑器
                editor.putString("username", "");
                editor.putString("password", "");
                editor.putString("accessToken", "");
                // 设置退出之后不自动登陆
                editor.putBoolean("autoLogin", false);
                editor.commit();// 提交修改
                Logger.v( "sp保存editor：" + editor);
                UserInfoActivity.this.finish();
                break;
            case R.id.rl_pass:
                Intent intent2 = new Intent();
                intent2.setClass(this, ChangePassActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_sex:
                showSexDialog();
                break;
            case R.id.rl_avatar:
                showDialog();
                break;
            case R.id.rl_identity:
                showIdDialog();
                break;
            /**
             * 修改用户名
             */
            case R.id.rl_name:
                Intent intent3 = new Intent();
                intent3.putExtra("tv_title", "修改用户名");
                intent3.putExtra("hint", "请输入新用户名");
                intent3.setClass(this, ChangeProfileActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_nikname:
                Intent intent4 = new Intent();
                intent4.putExtra("tv_title", "修改昵称");
                intent4.putExtra("hint", "请输入新昵称");
                intent4.setClass(this, ChangeProfileActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_school:
                Intent intent5 = new Intent();
                intent5.putExtra("tv_title", "修改学校");
                intent5.putExtra("hint", "请输入新学校");
                intent5.setClass(this, ChangeProfileActivity.class);
                startActivity(intent5);
                break;
        }

    }

    /**
     * 头像
     */
    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.alert_choice_photo, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void on_click(View v) {
        switch (v.getId()) {
            case R.id.openCamera:
                openCamera();
                break;
            case R.id.openPhones:
                openPhones();
                break;
            case R.id.cancel:
                dialog.cancel();
                break;
            default:
                break;
        }
    }

    // 打开相册
    private void openPhones() {
        Intent intentFromGallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }

    // 打开照相机
    private void openCamera() {
        // 打开相机
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用,存储缓存图片
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME_TEMP)));
        }
        startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:// 打开相册返回
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:// 打开相机返回
                    if (hasSdcard()) {
                        File tempFile = new File(IMGURL + IMAGE_FILE_NAME_TEMP);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESULT_REQUEST_CODE:// 裁剪完成,删除照相机缓存的图片
                    final File tempFile = new File(IMGURL + IMAGE_FILE_NAME_TEMP);
                    if (tempFile.exists()) {
                        new Thread() {
                            public void run() {
                                tempFile.delete();
                            }

                            ;
                        }.start();
                    }
                    // 保存截取后的图片
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap bitmap_photo = extras.getParcelable("data");
                            iv_avatar.setImageBitmap(bitmap_photo);
                            Logger.i("avatar"+ IMGURL + IMAGE_FILE_NAME);
                            try {
                                File f = new File(IMGURL + IMAGE_FILE_NAME);
                                if (!f.exists()) {
                                    f.createNewFile();
                                }
                                FileOutputStream fOut = new FileOutputStream(f);
                                bitmap_photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                fOut.flush();
                                fOut.close();
                                SharedPreferences sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, MODE_PRIVATE);
                                String id = sp.getString("id", null);
                                Log.d("UserInfoActivity", "修改头像时要上传到服务器的id参数输出:"+id);
                                uploadAvatarToNet(f, id);
                            } catch (IOException e) {
                                isExceptionForSDCard = true;
                                Log.d("UserInfoActivity", "储存卡已拔出");
                                e.printStackTrace();
                            }
                        }
                    }
                    dialog.cancel();// 关闭dialog
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    private void showSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        builder.setTitle("请选择性别");
        final String[] sex = {"男", "女", "保密"};
        Logger.i( tv_sex.getText().toString() + "");
        int checkedItem = 0;
        if (tv_sex.getText().toString().equals("男")) {
            checkedItem = 0;
        } else if (tv_sex.getText().toString().equals("女")) {
            checkedItem = 1;
        } else {
            checkedItem = 2;
        }

        /**
         * 设置一个单项选择下拉框
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认‘女‘ 会被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */
        builder.setSingleChoiceItems(sex, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sexx = (String) sex[which];
                Toast.makeText(UserInfoActivity.this, "性别为：" + sex[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_sex.setText(sexx);
            }
        });
        builder.show();
    }

    private void showIdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        builder.setTitle("请选择身份");
        final String[] sex = {"教师", "学生", "其他"};
        Logger.i( tv_identity.getText().toString() + "");
        int checkedItem = 0;
        if (tv_identity.getText().toString().equals("教师")) {
            checkedItem = 0;
        } else if (tv_identity.getText().toString().equals("学生")) {
            checkedItem = 1;
        } else {
            checkedItem = 2;
        }

        // 设置一个单项选择下拉框
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认‘女‘ 会被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */
        builder.setSingleChoiceItems(sex, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Identity = (String) sex[which];
                Toast.makeText(UserInfoActivity.this, "身份为：" + sex[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_identity.setText(Identity);
            }
        });
        builder.show();
    }

    /**
     * 将头像图片上传到服务器
     * @param file_avatar -> 要上传的头像的文件
     */
    private void uploadAvatarToNet(File file_avatar, String user_id) {
        if (isExceptionForSDCard) {
            Toast.makeText(UserInfoActivity.this, "内存卡已拔出", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.showWithStatus("请稍等...");
        //File file = new File("/storage/sdcard0/faceImage.jpg");//访问手机端的文件资源，保证手机端sdcdrd中必须有这个文件

        //定义请求中的字符串字段
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);

        //定义要上传的图片文件
        RequestBody requestBodyImageFile = RequestBody.create(MediaType.parse("multipart/form-data"), file_avatar);
        MultipartBody.Part part = MultipartBody.Part.createFormData("img_file", file_avatar.getName(), requestBodyImageFile);


        Call<ResponseBody> call = APIWrapper.getInstance().uploadAvatarToNet(requestFile, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("UserInfoActivity", "response返回的message:"+response.message());
                Log.d("UserInfoActivity", "response对象的isSuccessful()函数的输出:"+String.valueOf(response.isSuccessful()));
                if (response.isSuccessful()) {
                    File file_native_avatar = isExistsNativeFile(avatar_file_name);
                    if (file_native_avatar.exists()) {
                        file_native_avatar.delete();
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, MODE_PRIVATE);
        String nickname = sp.getString("nickname", null);
        String name = sp.getString("name", null);
        tv_nickname.setText(nickname);
        tv_name.setText(name);
    }
}
