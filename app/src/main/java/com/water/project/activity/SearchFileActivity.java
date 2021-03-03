package com.water.project.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.water.project.R;
import com.water.project.adapter.SearchTxtAdapter;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.pinyin.CharacterParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchFileActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.et_key)
    EditText etKey;
    @BindView(R.id.listView)
    ListView listView;
    //存放.txt的文件集合
    private List<File> txtList=new ArrayList<>();
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_file);
        ButterKnife.bind(this);
        initView();
        //开始搜索txt文件
        DialogUtils.showProgress2(SearchFileActivity.this,"正在搜索txt文件，请稍等");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                File path= Environment.getExternalStorageDirectory();
                File[] files=path.listFiles();
                getFileName(files);
                listView.setAdapter(new SearchTxtAdapter(SearchFileActivity.this,txtList));
                DialogUtils.closeProgress();
            }
        },2000);
    }

    @OnClick(R.id.lin_back)
    public void onViewClicked() {
        finish();
    }


    /**
     * 初始化
     */
    private void initView(){
        tvHead.setText("搜索文件");
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        /**
         * 监听搜索框
         */
        etKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
               //根据关键字查询文件
                getFileByKey(s.toString());
            }
        });
    }



    /**
     * 搜索本地.txt文件
     * @param files
     */
    private void getFileName(File[] files){
        if(files==null){
            ToastUtil.showLong("文件夹是空的");
            return;
        }
        for (int i=0,len=files.length;i<len;i++){
            if(files[i].isDirectory()){
                getFileName(files[i].listFiles());
            }else{
                String fileName=files[i].getName();
                if(fileName.endsWith(".txt") && files[i].length()!=0){
                    txtList.add(files[i]);
                }
            }
        }
    }



    /**
     * 根据关键字查询文件
     * @param keys
     */
    private void getFileByKey(String keys) {
        if(txtList.size()==0){
            return;
        }
        if(TextUtils.isEmpty(keys)){
            listView.setAdapter(new SearchTxtAdapter(this,txtList));
            return;
        }
        List<File> list = new ArrayList<>();
        for (int i = 0; i < txtList.size(); i++) {
             String name=txtList.get(i).getName();
             if (name.toUpperCase().indexOf(keys.toUpperCase()) != -1 || characterParser.getSelling(name).toUpperCase().startsWith(keys.toUpperCase())) {
                 list.add(txtList.get(i));
             }
        }
        listView.setAdapter(new SearchTxtAdapter(this,list));
    }
}
