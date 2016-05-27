package com.geostar.smackandroid.frag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geostar.smackandroid.AfterLoginActivity;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

public class ContactFragment extends BaseFragment implements RosterListener,PresenceListener{
	
	private ListView mContactList;
	private Roster mRoster;
	
	public interface OnContactClick{
		void OnContactClick(RosterEntry contact);
	}

	public ContactFragment(AfterLoginActivity activity) {
		super(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contacts, null);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mService = ((XMPPBinder)service).getService();
		mRoster = Roster.getInstanceFor(mService.getConnection());
		mRoster.addRosterListener(this);
		
		loadContactListToView();
	}

	private void loadContactListToView() {
		mContactList = (ListView) getView().findViewById(R.id.listView);
		if(mService != null && mService.getConnection() != null ){
			List<RosterEntry> rosters = new ArrayList<RosterEntry>();
			rosters.addAll(mRoster.getEntries());
			ContactAdapter adapter = new ContactAdapter(rosters);
			mContactList.setAdapter(adapter);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		loadContactListToView();
		super.onResume();
	}
	
	/* 
	 * ������ı� 
	 */
	
	@Override
	public void entriesAdded(Collection<String> addresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entriesUpdated(Collection<String> addresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entriesDeleted(Collection<String> addresses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void presenceChanged(Presence presence) {
		// TODO Auto-generated method stub
		
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
				convertView = View.inflate(parent.getContext(), R.layout.contact_item, null);
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
		
	}

}
