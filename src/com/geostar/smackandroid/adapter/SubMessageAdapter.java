package com.geostar.smackandroid.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.geostar.smackandroid.R;

public class SubMessageAdapter<T> extends BaseAdapter {

	private List<T> mDatas;
	private Context mContext;
	
	public SubMessageAdapter(Context context,List<T> mDatas, ContentProv<T> mContentGetter) {
		super();
		this.mDatas = mDatas;
		this.mContentGetter = mContentGetter;
		mContext = context;
	}

	public interface ContentProv<T>{
		String getText1(T obj);
		String getText2(T obj);
		OnClickListener getBtnClick(T obj);
	}
	
	private ContentProv<T> mContentGetter = null;
	
	public void setContentGetter(ContentProv<T> getter){
		mContentGetter = getter;
	}
	
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView ==  null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.pubs_discover_all_item, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		if( mContentGetter != null ){
			T item = getItem(position);
			holder.title.setText(mContentGetter.getText1(item));
//			holder.desc.setText(mContentGetter.getText2(item));
			holder.desc.setVisibility(View.GONE);
			holder.addSubs.setOnClickListener(mContentGetter.getBtnClick(item));
			holder.addSubs.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	
	class Holder {
		TextView title;
		TextView desc;
		Button addSubs;
		public Holder(View convertView) {
			this.title = (TextView) convertView.findViewById(R.id.tv_nodename);
			this.desc = (TextView) convertView.findViewById(R.id.tv_desc);
			this.addSubs = (Button) convertView.findViewById(R.id.btn_subcrible);
		}
	}

}
