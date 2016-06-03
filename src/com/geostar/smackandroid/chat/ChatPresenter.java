package com.geostar.smackandroid.chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import com.geostar.smackandroid.utils.Utils;


public class ChatPresenter implements ChatContract.Presenter, FileTransferListener{

	private Roster mRoster;
	
	private AbstractXMPPConnection mConnection;
	
	private ChatContract.View mView;
	
	private FileTransferManager mFileTransManager;

	
	public ChatPresenter(AbstractXMPPConnection conn, ChatContract.View view) {
		super();
		this.mConnection = conn;
		this.mView = view;
		view.setPresenter(this);
		if(mConnection != null && mConnection.isConnected()){
			if(mFileTransManager == null){
				mFileTransManager = FileTransferManager.getInstanceFor(mConnection);
				mFileTransManager.addFileTransferListener(this);
			}
		}else{
			System.out.println("------ ERROR: connnect not connected!!!");
		}
	}

	@Override
	public void start() {
		if(mFileTransManager == null){
			mFileTransManager = FileTransferManager.getInstanceFor(mConnection);
			mFileTransManager.addFileTransferListener(this);
		}
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

	
	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		Utils.logDebug("Get A file Transfer Request,FileName:" + request.getFileName());
		IncomingFileTransfer inft = request.accept();
		try {
			inft.recieveFile(new File("/sdcard/" +request.getFileName()));
		} catch (SmackException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
