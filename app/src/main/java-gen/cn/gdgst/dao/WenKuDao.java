package cn.gdgst.dao;

import android.database.Cursor;

import cn.gdgst.entity.WenKu;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "wenku_list".
*/
public class WenKuDao extends AbstractDao<WenKu, Long> {

    public static final String TABLENAME = "wenku_list";

    /**
     * Properties of entity WenKu.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Gid = new Property(1, String.class, "gid", false, "GID");
        public final static Property Cateid = new Property(2, String.class, "cateid", false, "CATEID");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Img_url = new Property(4, String.class, "img_url", false, "IMG_URL");
        public final static Property Img_url_s = new Property(5, String.class, "img_url_s", false, "IMG_URL_S");
        public final static Property File_url = new Property(6, String.class, "file_url", false, "FILE_URL");
        public final static Property Time = new Property(7, String.class, "time", false, "TIME");
    };


    public WenKuDao(DaoConfig config) {
        super(config);
    }
    
    public WenKuDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"wenku_list\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"GID\" TEXT," + // 1: gid
                "\"CATEID\" TEXT," + // 2: cateid
                "\"TITLE\" TEXT," + // 3: title
                "\"IMG_URL\" TEXT," + // 4: img_url
                "\"IMG_URL_S\" TEXT," + // 5: img_url_s
                "\"FILE_URL\" TEXT," + // 6: file_url
                "\"TIME\" TEXT);"); // 7: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"wenku_list\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, WenKu entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String gid = entity.getGid();
        if (gid != null) {
            stmt.bindString(2, gid);
        }
 
        String cateid = entity.getCateid();
        if (cateid != null) {
            stmt.bindString(3, cateid);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String img_url = entity.getImg_url();
        if (img_url != null) {
            stmt.bindString(5, img_url);
        }
 
        String img_url_s = entity.getImg_url_s();
        if (img_url_s != null) {
            stmt.bindString(6, img_url_s);
        }
 
        String file_url = entity.getFile_url();
        if (file_url != null) {
            stmt.bindString(7, file_url);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(8, time);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public WenKu readEntity(Cursor cursor, int offset) {
        WenKu entity = new WenKu( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // gid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // cateid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // img_url
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // img_url_s
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // file_url
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // time
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, WenKu entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCateid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setImg_url(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setImg_url_s(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFile_url(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTime(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(WenKu entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(WenKu entity) {
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
