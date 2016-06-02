package com.geostar.smackandroid.contacts;

import java.util.List;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.base.BaseView;

public interface RosterContract {

	interface View extends BaseView<Presenter> {

		void showContactList(List<RosterEntry> contacts);

		void updateContactList(List<RosterEntry> contacts);
		
		void updateContactListFromBackground(List<RosterEntry> contacts);
		
//		void updataContactPresenceState(String who,Presence presence);

	}

	interface Presenter extends BasePresenter {

		Roster getRoster();
		
		List<RosterEntry> getAllRosterEntrys();
		
	}
	
}
