package com.geostar.smackandroid.contacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geostar.smackandroid.BaseFragment;
import com.geostar.smackandroid.MainActivity;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.chat.ChatActivity;

public class ContactFragment extends BaseFragment implements RosterListener,PresenceListener,OnRefreshListener{
	
	private Roster mRoster;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private List<RosterEntry> mRostersData = new ArrayList<RosterEntry>();
	private ContactAdapter mAdapter;
	
	
	public interface OnContactClick{
		void onContactClick(RosterEntry contact);
	}

	public ContactFragment(MainActivity activity) {
		super(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_contacts, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getListView().setOnItemClickListener(mOnContactClick);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
//		getXMPPService() = ((XMPPBinder)service).getService();
		super.onServiceConnected(name, service);
		mRoster = Roster.getInstanceFor(getXMPPService().getXMPPConnection());
		mRoster.addRosterListener(this);
		
		loadContactListToView();
	}


	private void loadContactListToView() {
		if(getXMPPService() != null && getXMPPService().getXMPPConnection() != null ){
			mRostersData = new ArrayList<RosterEntry>();
			mRostersData.addAll(mRoster.getEntries());

			mAdapter = new ContactAdapter(mRostersData);
			if( getView() != null){
				getListView().setAdapter(mAdapter);
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		super.onServiceDisconnected(name);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		loadContactListToView();
		super.onResume();
	}
	
	
	@Override
	public void entriesAdded(Collection<String> addresses) {
		// TODO Auto-generated method stub
		loadContactListToView();
	}

	@Override
	public void entriesUpdated(Collection<String> addresses) {
		// TODO Auto-generated method stub
		loadContactListToView();
		
	}

	@Override
	public void entriesDeleted(Collection<String> addresses) {
		// TODO Auto-generated method stub
		loadContactListToView();
		
	}

	@Override
	public void presenceChanged(Presence presence) {
		// TODO Auto-generated method stub
		getListView().post(new Runnable() {
			
			@Override
			public void run() {
				loadContactListToView();
			}
		});
	}
	
	class ContactAdapter extends BaseAdapter{

		private List<RosterEntry> mData;
		
		public ContactAdapter(List<RosterEntry> data) {
			super();
			this.mData = data;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public RosterEntry getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(parent.getContext(), R.layout.listitem_contact, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			RosterEntry entry = getItem(position);
			holder.user.setText(entry.getUser());
			
			Presence pre = ContactFragment.this.mRoster.getPresence(entry.getUser());
			String nickname = TextUtils.isEmpty(entry.getName())?"":("("+ entry.getName()+")" );
			holder.state.setText(nickname + (pre.getStatus()==null?"":pre.getStatus()));
			return convertView;
		}
		
		class ViewHolder {
			TextView user;
			TextView state;
			
			ViewHolder(View convertView){
				user = (TextView) convertView.findViewById(R.id.tv_username);
				state = (TextView) convertView.findViewById(R.id.tv_presence);
			}
		}
		
	}

	@Override
	public void processPresence(Presence presence) {
		// TODO Auto-generated method stub
		loadContactListToView();
	}

	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadContactListToView();
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	private OnItemClickListener mOnContactClick = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			RosterEntry touser = mRostersData.get(position);
			if(touser == null){
				return ;
			}
			Intent intent = new Intent(getActivity(),ChatActivity.class);
			intent.putExtra("user",touser.getUser() );
			startActivity(intent);
		}
		
	};

}