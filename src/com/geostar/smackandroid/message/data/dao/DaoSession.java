package com.geostar.smackandroid.message.data.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.geostar.smackandroid.message.data.dao.ChatMessage;

import com.geostar.smackandroid.message.data.dao.ChatMessageDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig chatMessageDaoConfig;

    private final ChatMessageDao chatMessageDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        chatMessageDaoConfig = daoConfigMap.get(ChatMessageDao.class).clone();
        chatMessageDaoConfig.initIdentityScope(type);

        chatMessageDao = new ChatMessageDao(chatMessageDaoConfig, this);

        registerDao(ChatMessage.class, chatMessageDao);
    }
    
    public void clear() {
        chatMessageDaoConfig.getIdentityScope().clear();
    }

    public ChatMessageDao getChatMessageDao() {
        return chatMessageDao;
    }

}
