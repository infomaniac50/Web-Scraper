package infomaniac50.webscraper.storage;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class helps open, create, and upgrade the database file.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "WebScraperDatabaseHelper";
    private static final String DATABASE_NAME = "web_scraper.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SCRAPER_TABLE_NAME = "scrapers";
    
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SCRAPER_TABLE_NAME + " ("
                + WebScraper.KEY_ID + " INTEGER PRIMARY KEY,"
                + WebScraper.KEY_NAME + " TEXT,"
                + WebScraper.KEY_URL + " TEXT,"
                + WebScraper.KEY_EXPRESSION + " TEXT,"
                + WebScraper.KEY_INTERVAL + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SCRAPER_TABLE_NAME);
        onCreate(db);
    }
}