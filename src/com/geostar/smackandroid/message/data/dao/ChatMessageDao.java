package com.geostar.smackandroid.message.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "chat_message_table".
*/
public class ChatMessageDao extends AbstractDao<ChatMessage, Long> {

    public static final String TABLENAME = "chat_message_table";

    /**
     * Properties of entity ChatMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property From = new Property(1, String.class, "from", false, "from");
        public final static Property To = new Property(2, String.class, "to", false, "to");
        public final static Property Time = new Property(3, Long.class, "time", false, "time");
        public final static Property Type = new Property(4, String.class, "type", false, "type");
        public final static Property Subject = new Property(5, String.class, "subject", false, "subject");
        public final static Property Body = new Property(6, String.class, "body", false, "body");
        public final static Property Thread = new Property(7, String.class, "thread", false, "thread");
        public final static Property ExtentedContent = new Property(8, String.class, "extentedContent", false, "extentedContent");
        public final static Property ServerTime = new Property(9, Long.class, "serverTime", false, "server_time");
    };


    public ChatMessageDao(DaoConfig config) {
        super(config);
    }
    
    public ChatMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"chat_message_table\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"from\" TEXT," + // 1: from
                "\"to\" TEXT," + // 2: to
                "\"time\" INTEGER," + // 3: time
                "\"type\" TEXT," + // 4: type
                "\"subject\" TEXT," + // 5: subject
                "\"body\" TEXT," + // 6: body
                "\"thread\" TEXT," + // 7: thread
                "\"extentedContent\" TEXT," + // 8: extentedContent
                "\"server_time\" INTEGER);"); // 9: serverTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"chat_message_table\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ChatMessage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(2, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(3, to);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(4, time);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String subject = entity.getSubject();
        if (subject != null) {
            stmt.bindString(6, subject);
        }
 
        String body = entity.getBody();
        if (body != null) {
            stmt.bindString(7, body);
        }
 
        String thread = entity.getThread();
        if (thread != null) {
            stmt.bindString(8, thread);
        }
 
        String extentedContent = entity.getExtentedContent();
        if (extentedContent != null) {
            stmt.bindString(9, extentedContent);
        }
 
        Long serverTime = entity.getServerTime();
        if (serverTime != null) {
            stmt.bindLong(10, serverTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ChatMessage readEntity(Cursor cursor, int offset) {
        ChatMessage entity = new ChatMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // from
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // to
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // subject
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // body
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // thread
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // extentedContent
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9) // serverTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ChatMessage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFrom(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTo(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSubject(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBody(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setThread(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setExtentedContent(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setServerTime(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ChatMessage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ChatMessage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
