package cn.amose.yuzhong.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import cn.amose.yuzhong.util.Constant;

public class DatabaseManager {
	private static final String DATABASE_NAME = "yuzhong.sqlite";
	private static final int DATABASE_VERSION = 1;
	private static DatabaseManager sInstance;
	private static SQLiteDatabase sDatabase;

	public static DatabaseManager initialize(Context context) {
		if (sInstance == null) {
			sInstance = new DatabaseManager(context.getApplicationContext());
		}
		return sInstance;
	}

	public static void close() {
		if (sInstance != null && sDatabase.isOpen()) {
			sDatabase.close();
		}
		sInstance = null;
	}

	public synchronized static SQLiteDatabase getDatabase(Context context) {
		if (sDatabase == null) {
			sInstance = new DatabaseManager(context.getApplicationContext());
		}
		return sDatabase;
	}

	private DatabaseManager(Context context) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context,
				DATABASE_NAME);
		sDatabase = databaseHelper.getWritableDatabase();
		if (Constant.DEBUG) {
			dump();
		}
	}

	private void dump() {
		try {
			File storageDirectory = Environment.getExternalStorageDirectory();
			if (storageDirectory.exists()) {
				File appDirectory = new File(storageDirectory, DATABASE_NAME);
				if (!appDirectory.exists()) {
					appDirectory.mkdirs();
				}
				CopyFile.doCopyFile(sDatabase.getPath(),
						appDirectory.getAbsolutePath() + "/" + DATABASE_NAME);
				System.out.println(DATABASE_NAME
						+ " database copy is completed ");
			} else {
				System.out.println("sdcard not mount.");
			}

		} catch (IOException e) {
			System.out.println(DATABASE_NAME + " database copy is failed ");
			e.printStackTrace();
		}
	}

	/**
	 * Sqlite insert query
	 * 
	 * @param table
	 * @param values
	 * @return boolean result
	 */
	public static long insert(String table, ContentValues values) {
		return sDatabase.insert(table, null, values);
	}

	/**
	 * Sqlite update query
	 * 
	 * @param table
	 * @param values
	 * @param whereClause
	 * @return boolean result
	 */
	public static boolean update(String table, ContentValues values,
			String whereClause) {
		return sDatabase.update(table, values, whereClause, null) > 0;
	}

	/**
	 * Sqlite delete query
	 * 
	 * @param table
	 * @param whereClause
	 * @return boolean result
	 */
	public synchronized static boolean delete(String table, String whereClause) {
		return sDatabase.delete(table, whereClause, null) > 0;
	}

	/**
	 * Sqlite select query
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return cursor
	 */
	public static Cursor select(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		return sDatabase.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy, limit);
	}

	/**
	 * Sqlite select query
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return cursor
	 */
	public static Cursor select(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		return sDatabase.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
	}

	/**
	 * Sqlite select query
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return cursor
	 */
	public static Cursor select(String sql, String[] selectionArgs) {
		return sDatabase.rawQuery(sql, selectionArgs);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final String CREATE_TABLE_FOLLOWING = "CREATE TABLE following("
				+ "photo_id INTEGER PRIMARY KEY NOT NULL,"
				+ "user_id INTEGER NOT NULL,"
				+ "avatar_url TEXT,"
				+ "nickname TEXT NOT NULL,"
				+ "photo_url TEXT NOT NULL,"
				+ "description TEXT NOT NULL,"
				+ "date TIMESTAMP NOT NULL,"
				+ "likes INTEGER,"
				+ "comments INTEGER,"
				+ "liked BOOLEAN,"
				+ "width INTEGER,"
				+ "height INTEGER,"
				+ "price TEXT,"
				+ "click_url TEXT," + "storage_time TIMESTAMP NOT NULL);";

		public DatabaseHelper(Context context, String databaseName) {
			// calls the super constructor, requesting the default cursor
			// factory.
			super(context, databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// db.execSQL(CREATE_TABLE_FOLLOWING);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// apply necessary change of database
		}
	}

	/**
	 * copy database-file for test programming
	 * 
	 * @author ligl
	 * 
	 */
	private static class CopyFile {
		public static void doCopyFile(String src, String dest)
				throws IOException {
			File srcFile = new File(src);
			File destFile = new File(dest);
			FileInputStream input = new FileInputStream(srcFile);
			try {
				FileOutputStream output = new FileOutputStream(destFile);
				try {
					byte[] buffer = new byte[4096];
					int n = 0;
					while (-1 != (n = input.read(buffer))) {
						output.write(buffer, 0, n);
					}
				} finally {
					try {
						if (output != null) {
							output.close();
						}
					} catch (IOException ioe) {
						// ignore
					}
				}
			} finally {
				try {
					if (input != null) {
						input.close();
					}
				} catch (IOException ioe) {
					// ignore
				}
			}
		}

	}
}
