package com.water.project.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.adapter.SearchOldAdapter;
import com.water.project.bean.OldData;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史数据查询
 * Created by Administrator on 2018/7/5 0005.
 */

public class SearchOldActivity extends BaseActivity implements View.OnClickListener{

    private EditText etName,etCode;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_search_old);
        initView();
    }


    /**
     * 初始化数据
     */
    private void initView(){
        TextView textView=(TextView)findViewById(R.id.tv_head);
        textView.setText("历史数据查询");
        etName=(EditText)findViewById(R.id.et_aso_name);
        etCode=(EditText)findViewById(R.id.et_aso_code);
        ListView listView=(ListView)findViewById(R.id.listView) ;
        findViewById(R.id.tv_search1).setOnClickListener(this);
        findViewById(R.id.tv_search2).setOnClickListener(this);

        List<OldData> list=new ArrayList<>();
        OldData oldData1=new OldData("187229871209","2.07","27","28.084","10.189");
        OldData oldData2=new OldData("123789822287","2.17","24","24.004","11.123");
        OldData oldData3=new OldData("109387628987","2.05","26","25.084","10.852");
        OldData oldData4=new OldData("138498748877","1.57","19","22.901","10.456");
        OldData oldData5=new OldData("159022772987","2.27","17","25.859","9.789");
        OldData oldData6=new OldData("102987773309","1.05","22","27.258","9.147");
        OldData oldData7=new OldData("100222987374","1.56","30","30.963","10.654");
        OldData oldData8=new OldData("199222887337","2.00","27","30.254","11.123");
        list.add(oldData1);
        list.add(oldData8);
        list.add(oldData2);
        list.add(oldData3);
        list.add(oldData4);
        list.add(oldData5);
        list.add(oldData6);
        list.add(oldData7);
        listView.setAdapter(new SearchOldAdapter(mContext,list));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search1:
                 final String name=etName.getText().toString().trim();
                 if(TextUtils.isEmpty(name)){
                     showToastView("请输入项目名称！");
                 }
                 break;
            case R.id.tv_search2:
                final String code=etCode.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    showToastView("请输入项目名称！");
                }
                 break;
            default:
                break;
        }

    }
}
