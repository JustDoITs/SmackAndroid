package com.geostar.smackandroid.pubsub.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smackx.pubsub.Affiliation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.geostar.smackandroid.R;

public class SubsAdapter extends BaseAdapter {

	private List<Affiliation> mDatas;
	private Context mContext;
	private Map<String,Integer> hasNewMessageNode = new HashMap<String,Integer>();
 	
	public SubsAdapter(Context context,List<Affiliation> mDatas) {
		super();
		this.mDatas = mDatas;
		mContext = context;
	}

	public interface ContentProv<T>{
		String getText1(T obj);
		String getText2(T obj);
		OnClickListener getBtnClick(T obj);
		void setHasNewMessage(Button msgIndicator, T obj);
	}
	
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Affiliation getItem(int position) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_pubs_node_item, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		Affiliation item = getItem(position);
		holder.title.setText(item.getNodeId());
		holder.desc.setText(item.getType().toString());
		holder.addSubs.setVisibility(View.GONE);
		if(hasNewMessageNode.keySet().contains(item.getNodeId())){
			holder.redDot.setText(hasNewMessageNode.get(item.getNodeId())  + "");
			holder.redDot.setVisibility(View.VISIBLE);
		}else{
			holder.redDot.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	/** 添加未读计数 */
	public void addNewMessageFlag(String nodeId,int num){
		int unreadTotal = num;
		if(hasNewMessageNode.containsKey(nodeId)){
			unreadTotal += hasNewMessageNode.get(nodeId).intValue();
			
		}
		hasNewMessageNode.put(nodeId, unreadTotal);
	}
	
	public void removeNewMessgeFlag(String nodeId){
		hasNewMessageNode.remove(nodeId);
	}
	
	class Holder {
		TextView title;
		TextView desc;
		Button addSubs;
		Button redDot;
		public Holder(View convertView) {
			this.title = (TextView) convertView.findViewById(R.id.tv_nodename);
			this.desc = (TextView) convertView.findViewById(R.id.tv_desc);
			this.addSubs = (Button) convertView.findViewById(R.id.btn_subcrible);
			this.redDot = (Button) convertView.findViewById(R.id.btn_redot);
		}
	}

}
