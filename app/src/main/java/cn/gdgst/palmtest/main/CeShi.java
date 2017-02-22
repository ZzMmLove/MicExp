package cn.gdgst.palmtest.main;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.gdgst.palmtest.R;

/**
 * Created by Don on 2016/8/22.
 */
public class CeShi extends Activity {
    private EditText edt;
    private Button btn_lishu,btn_kaishu;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ceshi);
        edt=(EditText)findViewById(R.id.edt);
        btn_lishu= (Button) findViewById(R.id.btn_lishu);
        btn_kaishu= (Button) findViewById(R.id.btn_kaishu);
        tv= (TextView) findViewById(R.id.tv);

        btn_lishu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AssetManager mgr=getAssets();//得到AssetManager
                Typeface tf=Typeface.createFromAsset(mgr, "fonts/lishu.ttf");//根据路径得到Typeface
                tv.setTypeface(tf);//设置字体
                String avalue = edt.getText().toString();//获取输入内容

                tv.setText(avalue);//输出显示
            }
        });
        btn_kaishu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AssetManager mgr=getAssets();//得到AssetManager
                Typeface tf=Typeface.createFromAsset(mgr, "fonts/kaishu.ttf");//根据路径得到Typeface
                tv.setTypeface(tf);//设置字体

                String avalue = edt.getText().toString();//获取输入内容
                tv.setText(avalue);//输出显示
            }
        });

    }


}
