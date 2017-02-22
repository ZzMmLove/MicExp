package cn.gdgst.palmtest.tab1.examsystem;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import cn.gdgst.entity.ExamTopic;
import cn.gdgst.palmtest.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ExamTopic examTopic;
    private View view;
    private String selected;

    public ReadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ReadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReadFragment newInstance(ExamTopic examTopic) {
        ReadFragment fragment = new ReadFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, examTopic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            examTopic = (ExamTopic) getArguments().getSerializable(ARG_PARAM1);
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
        TextView textView_title = (TextView) view.findViewById(R.id.fragment_read_textView_title);
        final TextView textView_Selected_Show = (TextView) view.findViewById(R.id.fragment_read_textView_selected);
        if (selected != null) {
            textView_Selected_Show.setText("已选:("+selected+")");
        }
        textView_title.setText(String.valueOf(examTopic.getId())+". "+Html.fromHtml(examTopic.getTitle()));
        ImageView imageView = (ImageView) view.findViewById(R.id.fragment_read_imageView);
        loadImage(examTopic.getImg(), imageView);
        ListView listView_answer = (ListView) view.findViewById(R.id.fragment_read_ListView);
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
    }

    /**
     * 采用Universal-Image-Loader图片加载框架加载图片
     * @param url 要加载图片的名称
     * @param imageView
     */
    private void loadImage(String url, ImageView imageView) {
        DisplayImageOptions displayImageOptions;
        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();

        ImageLoader.getInstance().displayImage("http://www.shiyan360.cn"+url , imageView , displayImageOptions, null);
    }

    public String geSelected_result() {
        return selected;
    }
}
