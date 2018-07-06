package com.water.project.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private TextView tvName,tvCode;
    private List<String> data_list=new ArrayList<>();
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
        textView.setText("监测井资料查询");
        tvName=(TextView)findViewById(R.id.et_aso_name);
        tvCode=(TextView)findViewById(R.id.et_aso_code);
        tvName.setOnClickListener(this);
        tvCode.setOnClickListener(this);
        findViewById(R.id.tv_search1).setOnClickListener(this);
        findViewById(R.id.tv_search2).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.et_aso_name:
                 View view= LayoutInflater.from(mContext).inflate(R.layout.xiala_listview,null);
                 bottomPopupWindow(0,0,view,tvName);
                 ListView listView=(ListView)view.findViewById(R.id.listView);
                 data_list.clear();
                 data_list.add("1102745271");
                 data_list.add("1102745272");
                 data_list.add("1102745273");
                 data_list.add("1102745274");
                 ArrayAdapter arrayAdapter=new ArrayAdapter(this, R.layout.item_text, data_list);
                 listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                         mPopuwindow.dismiss();
                         tvName.setText(data_list.get(position));
                    }
                });
                 break;
            case R.id.et_aso_code:
                View view2= LayoutInflater.from(mContext).inflate(R.layout.xiala_listview,null);
                bottomPopupWindow(0,0,view2,tvCode);
                ListView listView2=(ListView)view2.findViewById(R.id.listView);
                data_list.clear();
                data_list.add("1号观测井");
                data_list.add("2号观测井");
                data_list.add("3号观测井");
                data_list.add("4号观测井");
                ArrayAdapter arrayAdapter2=new ArrayAdapter(this, R.layout.item_text, data_list);
                listView2.setAdapter(arrayAdapter2);
                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mPopuwindow.dismiss();
                        tvCode.setText(data_list.get(position));
                    }
                });
                 break;
            case R.id.tv_search1:
                 final String name=tvName.getText().toString().trim();
                 if(TextUtils.isEmpty(name)){
                     showToastView("请输入项目名称！");
                 }
                 break;
            case R.id.tv_search2:
                final String code=tvCode.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    showToastView("请输入项目名称！");
                }
                 break;
            case R.id.lin_back:
                finish();
                break;
            default:
                break;
        }

    }
}
