package cn.gdgst.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.database.Database;
import de.greenrobot.dao.database.DatabaseOpenHelper;
import de.greenrobot.dao.database.EncryptedDatabaseOpenHelper;
import de.greenrobot.dao.database.StandardDatabase;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 3): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 3;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        ChuangKeDao.createTable(db, ifNotExists);
        ZiXunDao.createTable(db, ifNotExists);
        WenKuDao.createTable(db, ifNotExists);
        MingShiDao.createTable(db, ifNotExists);
        PeiXunDao.createTable(db, ifNotExists);
        KaoShiDao.createTable(db, ifNotExists);
        HuiZhanDao.createTable(db, ifNotExists);
        ZhuangBeiDao.createTable(db, ifNotExists);
        VideoDao.createTable(db, ifNotExists);
        ScienceDao.createTable(db, ifNotExists);
        MidPhyDao.createTable(db, ifNotExists);
        MidBioDao.createTable(db, ifNotExists);
        MidCheDao.createTable(db, ifNotExists);
        ExperimentDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        ChuangKeDao.dropTable(db, ifExists);
        ZiXunDao.dropTable(db, ifExists);
        WenKuDao.dropTable(db, ifExists);
        MingShiDao.dropTable(db, ifExists);
        PeiXunDao.dropTable(db, ifExists);
        KaoShiDao.dropTable(db, ifExists);
        HuiZhanDao.dropTable(db, ifExists);
        ZhuangBeiDao.dropTable(db, ifExists);
        VideoDao.dropTable(db, ifExists);
        ScienceDao.dropTable(db, ifExists);
        MidPhyDao.dropTable(db, ifExists);
        MidBioDao.dropTable(db, ifExists);
        MidCheDao.dropTable(db, ifExists);
        ExperimentDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }
        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name,CursorFactory cursorFactory) {
            super(context, name,cursorFactory);
        }


        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public static abstract class EncryptedOpenHelper extends EncryptedDatabaseOpenHelper {
        public EncryptedOpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public EncryptedOpenHelper(Context context, String name, Object cursorFactory, boolean loadNativeLibs) {
            super(context, name, cursorFactory, SCHEMA_VERSION, loadNativeLibs);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class EncryptedDevOpenHelper extends EncryptedOpenHelper {
        public EncryptedDevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public EncryptedDevOpenHelper(Context context, String name, Object cursorFactory, boolean loadNativeLibs) {
            super(context, name, cursorFactory, loadNativeLibs);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(ChuangKeDao.class);
        registerDaoClass(ZiXunDao.class);
        registerDaoClass(WenKuDao.class);
        registerDaoClass(MingShiDao.class);
        registerDaoClass(PeiXunDao.class);
        registerDaoClass(KaoShiDao.class);
        registerDaoClass(HuiZhanDao.class);
        registerDaoClass(ZhuangBeiDao.class);
        registerDaoClass(VideoDao.class);
        registerDaoClass(ScienceDao.class);
        registerDaoClass(MidPhyDao.class);
        registerDaoClass(MidBioDao.class);
        registerDaoClass(MidCheDao.class);
        registerDaoClass(ExperimentDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}