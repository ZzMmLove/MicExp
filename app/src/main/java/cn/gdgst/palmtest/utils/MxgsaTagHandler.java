package cn.gdgst.palmtest.utils;

import org.xml.sax.XMLReader;

import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

public class MxgsaTagHandler implements TagHandler{
    private int sIndex = 0;  
    private  int eIndex=0;
  
    
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // TODO Auto-generated method stub
        if (tag.toLowerCase().equals("mxgsa")) {
            if (opening) {
                sIndex=output.length();
            }else {
                eIndex=output.length();
                output.setSpan(new MxgsaSpan(), sIndex, eIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
    private class MxgsaSpan extends ClickableSpan implements OnClickListener{
        @Override
        public void onClick(View widget) {
            // TODO Auto-generated method stub
        }
    }

}