package com.water.project.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.utils.photo.AlbumHelper;
import com.water.project.utils.photo.Bimp;
import com.water.project.utils.photo.BitmapCache;
import com.water.project.utils.photo.ImageBucket;
import com.water.project.utils.photo.ImageItem;
import com.water.project.utils.photo.PicturesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择照片
 * @author Administrator
 *
 */

public class ImageGridActivity extends BaseActivity {
	private List<ImageBucket> contentList;
	private List<ImageItem> dataList;
	private GridView gridView;
	private ImageGridAdapter adapter;
	private AlbumHelper helper;
	private Button bt;
	private int count;
	private LinearLayout linBack;
	private TextView title;
	private BitmapCache cache = new BitmapCache();
	private List<String> imgs=new ArrayList<String>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_grid);
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for(ImageBucket imgbucket:contentList){
			dataList.addAll(imgbucket.imageList);
		}
		ImageBucket bucket = new ImageBucket();
		bucket.bucketName = "All photos";
		bucket.imageList = dataList;
		bucket.count = bucket.imageList.size();
		contentList.add(0, bucket);
		initView();
	}

	private void initView() {
		linBack=(LinearLayout)findViewById(R.id.lin_back);
		title=(TextView)findViewById(R.id.tv_head);
		title.setText("选择图片");
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				adapter.notifyDataSetChanged();
			}
		});
		bt = (Button) findViewById(R.id.bt);
		if(Bimp.selectBitmap.size() != 0){
			count = Bimp.selectBitmap.size();
			bt.setText("确定" + "(" + count + ")");
		}
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent();
				setResult(PicturesUtil.CODE_GALLERY_REQUEST, intent);
				ImageGridActivity.this.finish();
			}
		});
		linBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				for (int i = 0; i <imgs.size(); i++) {
					 for (int j = 0; j < Bimp.selectBitmap.size(); j++) {
						 if(imgs.get(i).equals(Bimp.selectBitmap.get(j).getImagePath())){
							 Bimp.selectBitmap.remove(j);
						 }
					}
				}
				Bimp.imgList.clear();
				ImageGridActivity.this.finish();
			}
		});
	}

	
	class ImageGridAdapter extends BaseAdapter {
		BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
			public void imageLoad(ImageView imageView, Bitmap bitmap,
                                  Object... params) {
				if (imageView != null && bitmap != null) {
					String url = (String) params[0];
					if (url != null && url.equals((String) imageView.getTag())) {
						((ImageView) imageView).setImageBitmap(bitmap);
					} else {
					}
				} else {
				}
			}
		};
		
		public int getCount() {
			int count = 0;
			if (dataList != null) {
				count = dataList.size();
			}
			return count;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		class Holder {
			private ImageView iv;
			private ImageView selected;
			private TextView text;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = getLayoutInflater().inflate(R.layout.item_image_grid, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.image);
				holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
				holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			dataList.get(position).isSelected = false;
			for (int i = 0; i < Bimp.selectBitmap.size(); i++) {
				if (dataList.get(position).imageId.equals(Bimp.selectBitmap.get(i).imageId)) {
					dataList.get(position).isSelected = true;
					break;
				} else {
					dataList.get(position).isSelected = false;
				}
			}
			final ImageItem item = dataList.get(position);
			holder.iv.setTag(item.imagePath);
			cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,callback);
			if (item.isSelected) {
				holder.selected.setVisibility(View.VISIBLE);
				holder.selected.setImageResource(R.mipmap.select_yes);
			} else {
				holder.selected.setVisibility(View.INVISIBLE);
				holder.text.setBackgroundColor(0x00000000);
			}
			holder.iv.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int k=-1;
					for (int i = 0; i < Bimp.selectBitmap.size(); i++) {
						 if(v.getTag()!=null){
							 if(Bimp.selectBitmap.get(i).getImagePath().equals(v.getTag())){
								 k=i;
								 break;
							 }
						 }
					}
					if(k==-1){
						if(Bimp.selectBitmap.size()>=5){
							showToastView("图片最多选择9个！");
							return;
						}
						holder.selected.setVisibility(View.VISIBLE);
						dataList.get(position).setSelected(true);
						Bimp.selectBitmap.add(dataList.get(position));
						Bimp.imgList.add(dataList.get(position));
						holder.selected.setImageResource(R.mipmap.select_yes);
						imgs.add(dataList.get(position).getImagePath());
						bt.setText("确定"+ "(" + (Bimp.selectBitmap.size()) + ")");
					}else{
						holder.selected.setVisibility(View.INVISIBLE);
						dataList.get(position).setSelected(false);
						Bimp.selectBitmap.remove(dataList.get(position));
						imgs.remove(dataList.get(position));
						holder.text.setBackgroundColor(0x00000000);
						bt.setText("确定"+ "(" + (Bimp.selectBitmap.size()) + ")");
					}
				}
			});
			return convertView;
		}
	}
}
