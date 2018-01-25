package cn.gdgst.palmtest.tab1.examsystem;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.bean.ExamTopic;
import cn.gdgst.palmtest.bean.TExamTopic;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private int fragmentId;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private int content;
    private boolean isSaveTAnswer;
    private List<EditText> listEditText = new ArrayList<>();
    private int editTextCount;
    private ArrayList<String> arrayListT = new ArrayList<>();
    private String stringAnswer;

    private ExamTopic examTopic;
    /**
     * 选择题实体类对象
     */
    private TExamTopic tExamTopic;
    /**
     * 解答题实体类对象
     */
    private TExamTopic jExamTopic;
    private View view;
    private String selected;
    private HashMap<Integer, String> hashMapResult = new HashMap<>();
    /**
     * 记录当前fragment的状态，选择、填空、解答
     */
    private int currentFragmentState;

    public ReadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ReadFragment.
     */
    public static ReadFragment newInstance(ExamTopic examTopic, TExamTopic tExamTopic, TExamTopic jExamTopic, int content) {
        ReadFragment fragment = new ReadFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, examTopic);
        args.putSerializable(ARG_PARAM2, tExamTopic);
        args.putSerializable(ARG_PARAM3, jExamTopic);
        args.putSerializable(ARG_PARAM4, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            examTopic = (ExamTopic) getArguments().getSerializable(ARG_PARAM1);
            tExamTopic = (TExamTopic) getArguments().getSerializable(ARG_PARAM2);
            jExamTopic = (TExamTopic) getArguments().getSerializable(ARG_PARAM3);
            content = (int) getArguments().getSerializable(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_read, container, false);

        initView();
        // Inflate the layout for this fragment
        return view;
    }

    private void initView() {
        /**
         * 题目
         */
        TextView textView_title = (TextView) view.findViewById(R.id.fragment_read_textView_title);
        /**
         * 已经选择的答案
         */
        final TextView textView_Selected_Show = (TextView) view.findViewById(R.id.fragment_read_textView_selected);
        /**
         * 图片
         */
        ImageView imageView = (ImageView) view.findViewById(R.id.fragment_read_imageView);
        /**
         * 选项列表
         */
        ListView listView_answer = (ListView) view.findViewById(R.id.fragment_read_ListView);

        if (content ==1 ) {
            currentFragmentState = 1;
            if (selected != null) {
                textView_Selected_Show.setText("已选:("+selected+")");
            }
            textView_title.setText(String.valueOf(examTopic.getId())+". "+Html.fromHtml(examTopic.getTitle()));
            Log.d("ReadFragment", "打印要缓存图片的路径"+examTopic.toString());

            if (examTopic.getImg().equals("") || examTopic.getImg() == null){
                imageView.setVisibility(View.GONE);
            }else {
                imageView.setVisibility(View.VISIBLE);
                loadImage(examTopic.getImg(), imageView);
            }

            listView_answer.setAdapter(new AnswerAdapter(getActivity(), examTopic));
            listView_answer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            textView_Selected_Show.setText("已选:(A)");
                            selected = "A";
                            break;
                        case 1:
                            textView_Selected_Show.setText("已选:(B)");
                            selected = "B";
                            break;
                        case 2:
                            textView_Selected_Show.setText("已选:(C)");
                            selected = "C";
                            break;
                        case 3:
                            textView_Selected_Show.setText("已选:(D)");
                            selected = "D";
                            break;
                    }
                }
            });
        } else if (content == 2) {
            currentFragmentState = 2;
            textView_title.setText(tExamTopic.getTitle());
            textView_Selected_Show.setVisibility(View.GONE);
            listView_answer.setVisibility(View.GONE);
            if ("".equals(tExamTopic.getImg()) || tExamTopic.getImg() == null){
                imageView.setVisibility(View.GONE);
            }else {
                imageView.setVisibility(View.VISIBLE);
                loadImage(tExamTopic.getImg(), imageView);
            }

            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_read_LinearLayout_container);
            editTextCount = getBracketCount(tExamTopic.getTitle());
            Log.d("ReadFragment", "要创建的EditText的数量:"+editTextCount);

            for (int a = 0; a < editTextCount; a ++) {
                final EditText editText = new EditText(getActivity());
                editText.setId(a + 1);
                editText.setBackgroundResource(R.drawable.bg_edit_answe);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        hashMapResult.put(editText.getId(), s.toString());
                    }
                });
                editText.setWidth(700);
                editText.setHint(String.valueOf(a+1));
                listEditText.add(editText);
                linearLayout.addView(editText);
                if (!arrayListT.isEmpty()) {
                    if (arrayListT.get(a) != null) {
                        editText.setText(arrayListT.get(a));
                    }
                }
            }
        } else if (content == 3) {
            currentFragmentState = 3;
            textView_title.setText(jExamTopic.getTitle());
            textView_Selected_Show.setVisibility(View.GONE);
            if ("".equals(jExamTopic.getImg()) || jExamTopic.getImg() == null){
                imageView.setVisibility(View.GONE);
            }else {
                imageView.setVisibility(View.VISIBLE);
                loadImage(jExamTopic.getImg(), imageView);
            }

            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_read_LinearLayout_container);
            EditText editTextJContent = new EditText(getActivity());
            editTextJContent.setWidth(700);
            editTextJContent.setBackgroundResource(R.drawable.bg_edit_answe);
            editTextJContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    stringAnswer = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    stringAnswer = s.toString();
                }
            });
            linearLayout.addView(editTextJContent);
            if (stringAnswer != null) {
                editTextJContent.setText(stringAnswer);
            }
        }

    }

    /**
     * 采用Universal-Image-Loader图片加载框架加载图片
     * @param url 要加载图片的名称
     * @param imageView
     */
    private void loadImage(String url, ImageView imageView) {
        String vaidImg = url.trim();
        DisplayImageOptions displayImageOptions;
        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();

        ImageLoader.getInstance().displayImage("http://www.shiyan360.cn"+vaidImg , imageView , displayImageOptions, null);
    }

    /**
     * 输入一串字符串用正则表达式的方式去计算返回有多少个括号
     * @param str 输入要计算括号数量的字符串
     * @return 返回括号数量(一对括号为一个)
     */
    private int getBracketCount(String str) {
        String reg = "\\(\\)|\\（\\）|\\(\\）|\\（\\)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        ArrayList<String> strs = new ArrayList<String>();
        int count = 0;
        while (matcher.find()) {
            strs.add(matcher.group());
            count++;
        }
        for (String s : strs){
            Log.d("ReadFragment", "括号输出:"+s);
        }
        Log.d("ReadFragment", "一共有多少对括号:"+count);
        return count;
    }

    public List<EditText> getListEditText() {
        return listEditText;
    }

    public String getSelected_result() {
        return selected;
    }

    public HashMap<Integer, String> getTAnswerList() {
        return hashMapResult;
    }

    public String getJAnswer() {
        return stringAnswer;
    }

    public int getCurrentFragmentState(){
        return currentFragmentState;
    }

    public int getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }
}
