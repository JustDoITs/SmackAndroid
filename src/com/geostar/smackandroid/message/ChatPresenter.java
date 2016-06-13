package com.geostar.smackandroid.message;

import java.io.File;
import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import com.geostar.smackandroid.utils.Utils;


public class ChatPresenter implements ChatContract.Presenter, FileTransferListener,StanzaListener{

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
			mConnection.removeAsyncStanzaListener(this);
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


	@Override
	public void sendFile(String filePath) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendMessage(String msg) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if(packet instanceof org.jivesoftware.smack.packet.Message){
            final org.jivesoftware.smack.packet.Message msg = ((org.jivesoftware.smack.packet.Message)packet);
//            Log.d(TAG,"Recv a message - MessageWithBodiesFilter : " +  msg.getBody());
//            getListView().post(new Runnable() {
//				
//				@Override
//				public void run() {
//					receiverANewMessage(msg);
//				}
//			});
        }
    }

	@Override
	public void destory() {
		if(mConnection != null){
			mConnection.removeAsyncStanzaListener(this);
		}
	}


}
