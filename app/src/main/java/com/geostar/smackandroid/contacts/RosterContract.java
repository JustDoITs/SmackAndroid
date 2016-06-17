package com.geostar.smackandroid.contacts;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.base.BaseView;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.List;

public interface RosterContract {

	interface View extends BaseView<Presenter> {

		void updateContactList(List<RosterGroup> contacts);
		
		void updateContactListFromBackground(List<RosterGroup> contacts);
		
		void showContactGrpList(List<RosterGroup> grps);
		
	}

	interface Presenter extends BasePresenter {

		Roster getRoster();
		
		List<RosterGroup> getRosterGroups();
		
		List<RosterEntry> getAllRosterEntrys();
		
	}
	
}
