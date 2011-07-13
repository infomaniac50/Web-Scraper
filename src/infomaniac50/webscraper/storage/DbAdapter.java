package infomaniac50.webscraper.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DbAdapter {
	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

    private static final String DATABASE_TABLE = "scrapers";
    
    public DbAdapter(Context context)
	{
		this.context = context;
	}
	
	public DbAdapter open() throws SQLException
	{
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public long createScraper(String Name, String URL, String Expression, int Interval)
	{
		ContentValues initialValues = createContentValues(Name,URL,Expression,Interval);
		
		return database.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public boolean updateScraper(long rowId, String Name, String URL, String Expression, int Interval)
	{
		ContentValues updateValues = createContentValues(Name,URL,Expression,Interval);
		
		return database.update(DATABASE_TABLE, updateValues, WebScraper.KEY_ID + "=" + rowId , null) > 0;		
	}
	
	public boolean deleteScraper(long rowId)
	{
		return database.delete(DATABASE_TABLE, WebScraper.KEY_ID + "=" + rowId, null) > 0;
	}
	
	public Cursor fetchAllScrapers()
	{
		return database.query(DATABASE_TABLE, new String[] {WebScraper.KEY_ID, WebScraper.KEY_NAME, WebScraper.KEY_URL, WebScraper.KEY_EXPRESSION, WebScraper.KEY_INTERVAL }, null, null, null, null, null);
	}
	
	public Cursor fetchScraper(long rowId) throws SQLException
	{
		Cursor mCursor = database.query(
				true, 
				DATABASE_TABLE, 
				new String[] {
						WebScraper.KEY_ID, 
						WebScraper.KEY_NAME, 
						WebScraper.KEY_URL, 
						WebScraper.KEY_EXPRESSION, 
						WebScraper.KEY_INTERVAL }, 
				WebScraper.KEY_ID + "=" + rowId, 
				null, 
				null, 
				null, 
				null, 
				null);
		
		return mCursor;
	}
	
	private ContentValues createContentValues(String Name, String URL, String Expression, int Interval)
	{
		ContentValues values = new ContentValues();
		values.put(WebScraper.KEY_NAME, Name);
		values.put(WebScraper.KEY_URL, URL);
		values.put(WebScraper.KEY_EXPRESSION, Expression);
		values.put(WebScraper.KEY_INTERVAL, Interval);
		
		return values;
	}
}
