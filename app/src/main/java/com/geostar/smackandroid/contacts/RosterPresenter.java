package com.geostar.smackandroid.contacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;

import com.geostar.smackandroid.contacts.RosterContract.View;


public class RosterPresenter implements RosterContract.Presenter ,RosterListener {

	private Roster mRoster;
	
	private AbstractXMPPConnection mConnection;
	
	private RosterContract.View mContactView;

	
	public RosterPresenter(AbstractXMPPConnection conn, View contactView) {
		super();
		this.mConnection = conn;
		this.mContactView = contactView;
		contactView.setPresenter(this);
		if(mConnection != null && mConnection.isConnected()){
			mRoster = Roster.getInstanceFor(mConnection);
			mRoster.addRosterListener(this);
			mContactView.showContactGrpList(getRosterGroups());
		}else{
			System.out.println("------ ERROR: connnect not connected!!!");
		}
	}

	@Override
	public void start() {
		mContactView.showContactGrpList(getRosterGroups());
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
	public Roster getRoster() {
		return mRoster;
	}

	@Override
	public void entriesAdded(Collection<String> addresses) {
		List<RosterGroup> ent = getRosterGroups();
		if(ent != null){
			mContactView.updateContactListFromBackground(ent);
		}
	}

	@Override
	public void entriesUpdated(Collection<String> addresses) {
		List<RosterGroup> ent = getRosterGroups();
		if(ent != null){
			mContactView.updateContactListFromBackground(ent);
		}
	}

	@Override
	public void entriesDeleted(Collection<String> addresses) {
		List<RosterGroup> ent = getRosterGroups();
		if(ent != null){
			mContactView.updateContactListFromBackground(ent);
		}
	}

	@Override
	public void presenceChanged(Presence presence) {
		List<RosterGroup> ent = getRosterGroups();
		if(ent != null){
			mContactView.updateContactListFromBackground(ent);
		}
	}
	
	public void updateContactLists() {
		List<RosterGroup> ent = getRosterGroups();
		if(ent != null){
			mContactView.updateContactList(ent);
		}
		Collection<RosterGroup> grps = getRoster().getGroups();
		
	}

	List<RosterGroup> grp;
	@Override
	public List<RosterGroup> getRosterGroups() {
		if(grp == null){
			grp = new ArrayList<RosterGroup>();
		}
		grp.clear();
		grp.addAll(mRoster.getGroups());
		return grp;
	}
	

}
