package com.geostar.smackandroid.chat;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;


public class ChatPresenter implements ChatContract.Presenter{

	private Roster mRoster;
	
	private AbstractXMPPConnection mConnection;
	
	private ChatContract.View mView;

	
	public ChatPresenter(AbstractXMPPConnection conn, ChatContract.View view) {
		super();
		this.mConnection = conn;
		this.mView = view;
		view.setPresenter(this);
		if(mConnection != null && mConnection.isConnected()){
			
		}else{
			System.out.println("------ ERROR: connnect not connected!!!");
		}
	}

	@Override
	public void start() {
		
	}
	
	
	public List<RosterEntry> getAllRosterEntrys() {
		if(mRoster != null){
			List<RosterEntry> entries = new ArrayList<RosterEntry>();
			entries.addAll( mRoster.getEntries() );
			return entries;
		}
		return null;
	}



	@Override
	public void sendFile(String filePath) {
		// TODO Auto-generated method stub
		
	}


}
