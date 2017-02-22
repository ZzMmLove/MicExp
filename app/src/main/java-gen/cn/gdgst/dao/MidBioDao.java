package cn.gdgst.dao;

import android.database.Cursor;

import cn.gdgst.entity.MidBio;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "midbio_list".
*/
public class MidBioDao extends AbstractDao<MidBio, Long> {

    public static final String TABLENAME = "midbio_list";

    /**
     * Properties of entity MidBio.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Gid = new Property(1, String.class, "gid", false, "GID");
        public final static Property Cateid = new Property(2, String.class, "cateid", false, "CATEID");
        public final static Property Gradeid = new Property(3, String.class, "gradeid", false, "GRADEID");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Img_url = new Property(5, String.class, "img_url", false, "IMG_URL");
        public final static Property Img_url_s = new Property(6, String.class, "img_url_s", false, "IMG_URL_S");
        public final static Property Video_url = new Property(7, String.class, "video_url", false, "VIDEO_URL");
        public final static Property Time = new Property(8, String.class, "time", false, "TIME");
    };


    public MidBioDao(DaoConfig config) {
        super(config);
    }
    
    public MidBioDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"midbio_list\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"GID\" TEXT," + // 1: gid
                "\"CATEID\" TEXT," + // 2: cateid
                "\"GRADEID\" TEXT," + // 3: gradeid
                "\"NAME\" TEXT," + // 4: name
                "\"IMG_URL\" TEXT," + // 5: img_url
                "\"IMG_URL_S\" TEXT," + // 6: img_url_s
                "\"VIDEO_URL\" TEXT," + // 7: video_url
                "\"TIME\" TEXT);"); // 8: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"midbio_list\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(DatabaseStatement stmt, MidBio entity) {
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
 
        String gradeid = entity.getGradeid();
        if (gradeid != null) {
            stmt.bindString(4, gradeid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String img_url = entity.getImg_url();
        if (img_url != null) {
            stmt.bindString(6, img_url);
        }
 
        String img_url_s = entity.getImg_url_s();
        if (img_url_s != null) {
            stmt.bindString(7, img_url_s);
        }
 
        String video_url = entity.getVideo_url();
        if (video_url != null) {
            stmt.bindString(8, video_url);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(9, time);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MidBio readEntity(Cursor cursor, int offset) {
        MidBio entity = new MidBio( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // gid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // cateid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gradeid
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // img_url
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // img_url_s
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // video_url
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // time
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MidBio entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCateid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGradeid(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setImg_url(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setImg_url_s(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setVideo_url(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTime(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MidBio entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MidBio entity) {
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
