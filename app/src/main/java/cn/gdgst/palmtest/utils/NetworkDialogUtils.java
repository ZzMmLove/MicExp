package cn.gdgst.palmtest.utils;

import android.content.Context;

import com.bigkoo.svprogresshud.SVProgressHUD;


public class NetworkDialogUtils {

    private static Context mContext;
    private static NetworkDialogUtils mInstance;
    private SVProgressHUD svProgressHUD;

    public static NetworkDialogUtils getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkDialogUtils(mContext);
        }
        return mInstance;
    }

    public static void init(Context ctx) {
        mInstance = new NetworkDialogUtils(ctx);
    }

    private NetworkDialogUtils(Context ctx) {
        mContext = ctx;
    }

    public void ShowNetworkDialog(){
        if(svProgressHUD == null){
            svProgressHUD = new SVProgressHUD(mContext);
        }
        svProgressHUD.showWithStatus("请稍等...");
    }

    public void HideNetworkDialog(){
        if(svProgressHUD.isShowing()){
            svProgressHUD.dismiss();
        }
    }
}
