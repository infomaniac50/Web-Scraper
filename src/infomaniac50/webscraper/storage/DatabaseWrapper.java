package infomaniac50.webscraper.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public final class DatabaseWrapper {
	private DbAdapter adapter;
	private boolean persistantConnection = false;
	private boolean isOpen = false;
	
	public DatabaseWrapper(Context context)
	{
		this(context, false);
	}
	
	public DatabaseWrapper(Context context, boolean persistantConnection)
	{
		adapter = new DbAdapter(context);
		this.persistantConnection = persistantConnection;
	}
	
	public boolean isPersistantConnection()
	{
		return persistantConnection;
	}
	
	public boolean isOpen()
	{
		return isOpen;
	}
	
	private void open()
	{
		if (!isOpen)
		{	
			adapter.open();
			isOpen = true;			
		}
	}
	
	public void close()
	{
		if (isOpen)
		{
			adapter.close();
			isOpen = false;
		}
	}
	
	private void done()
	{
		if (!persistantConnection)
		{
			close();
		}
	}
	
	public long createScraper(String Name, String URL, String Expression, int Interval)
	{
		long returnValue;
		open();
		returnValue = adapter.createScraper(Name, URL, Expression, Interval);
		done();
		return returnValue;
	}
	
	public boolean updateScraper(long rowId, String Name, String URL, String Expression, int Interval)
	{
		boolean returnValue;
		open();
		returnValue = adapter.updateScraper(rowId, Name, URL, Expression, Interval);
		done();
		return returnValue;
	}
	
	public boolean deleteScraper(long rowId)
	{
		boolean returnValue;
		open();
		returnValue = adapter.deleteScraper(rowId);
		done();
		return returnValue;
	}
	
	public Cursor fetchAllScrapers()
	{
		Cursor returnValue;
		open();
		returnValue = adapter.fetchAllScrapers();
		done();
		return returnValue;
	}
	
	public Cursor fetchScraper(long rowId) throws SQLException
	{
		Cursor returnValue;
		open();
		returnValue = adapter.fetchScraper(rowId);
		done();
		return returnValue;		
	}	
}
