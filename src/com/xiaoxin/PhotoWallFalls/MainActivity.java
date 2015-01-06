package com.xiaoxin.PhotoWallFalls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.bean.DuitangInfo;
import com.xiaoxin.PhotoWallFalls.SwipeRefresh.SwipeRefreshAndLoadLayout;
import com.xiaoxin.PhotoWallFalls.adapter.StaggeredAdapter;
import com.xiaoxin.PhotoWallFalls.utils.Helper;
import com.xiaoxin.PhotoWallFalls.utils.HttpUtils;
import com.xiaoxin.PhotoWallFalls.utils.HttpUtils.OnNetWorkResponse;
import com.xiaoxin.PhotoWallFalls.utils.ImageFetcher;


public class MainActivity extends Activity implements SwipeRefreshAndLoadLayout.OnRefreshListener {
	private ImageFetcher mImageFetcher;
	private StaggeredAdapter mAdapter;
	private SwipeRefreshAndLoadLayout swipeLayout;
	private List<DuitangInfo> duitangs;
	private int page;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageFetcher = new ImageFetcher(this, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		StaggeredGridView gridView = (StaggeredGridView) this.findViewById(R.id.staggeredGridView1);

		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		gridView.setFastScrollEnabled(true);

		mAdapter = new StaggeredAdapter(MainActivity.this, mImageFetcher);
		gridView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		AddItemToContainer1(1, 1);
		AddItemToContainer1(2, 1);
		AddItemToContainer1(3, 1);
		swipeLayout = (SwipeRefreshAndLoadLayout) this.findViewById(R.id.swipe_refresh);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setmMode(SwipeRefreshAndLoadLayout.Mode.BOTH);
		swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
	}

	@Override
    public void onRefresh() {
		Toast.makeText(this, "小新在加载神秘图片", 2000).show();
		AddItemToContainer1(1, 1);
		swipeLayout.setRefreshing(false);
    }
    @Override
    public void onLoadMore() {
    	Toast.makeText(this, "小新在加载神秘图片", 2000).show();
		AddItemToContainer1(page, 1);
		swipeLayout.setRefreshing(false);
    }
    
    private void AddItemToContainer1(final int pageindex, final int type) {
    	String url = "http://www.duitang.com/album/1733789/masn/p/" + pageindex + "/24/";
    	HttpUtils.RequestNetWork(url, new OnNetWorkResponse() {
			
			

			@Override
			public void ok(String response) {
				page=pageindex+1;
				duitangs = new ArrayList<DuitangInfo>();
				try {
					if (null != response) {
						JSONObject newsObject = new JSONObject(response);
						JSONObject jsonObject = newsObject.getJSONObject("data");
						JSONArray blogsJson = jsonObject.getJSONArray("blogs");

						for (int i = 0; i < blogsJson.length(); i++) {
							JSONObject newsInfoLeftObject = blogsJson.getJSONObject(i);
							DuitangInfo newsInfo1 = new DuitangInfo();
							newsInfo1.setAlbid(newsInfoLeftObject.isNull("albid") ? "" : newsInfoLeftObject.getString("albid"));
							newsInfo1.setIsrc(newsInfoLeftObject.isNull("isrc") ? "" : newsInfoLeftObject.getString("isrc"));
							newsInfo1.setMsg(newsInfoLeftObject.isNull("msg") ? "" : newsInfoLeftObject.getString("msg"));
							newsInfo1.setHeight(newsInfoLeftObject.getInt("iht"));
							duitangs.add(newsInfo1);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (type == 1) {

					mAdapter.addItemTop(duitangs);
					mAdapter.notifyDataSetChanged();

				} else if (type == 2) {
					mAdapter.addItemLast(duitangs);
					mAdapter.notifyDataSetChanged();

				}
			}
			
			@Override
			public void error(String error) {
				
			}
		});
    }

}
