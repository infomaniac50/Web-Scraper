package infomaniac50.webscraper.service;

import infomaniac50.webscraper.common.IStartable;
import infomaniac50.webscraper.common.IUpdatable;
import infomaniac50.webscraper.common.WebScraperUpdater;
import infomaniac50.webscraper.storage.DatabaseWrapper;
import infomaniac50.webscraper.storage.WebScraper;
import infomaniac50.webscraper.storage.DbAdapter;

import java.util.Hashtable;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;

public class ScraperService extends Service implements IStartable, IUpdatable{
	private static Intent intent;
	private Context context;
	private IBinder scraper_binder = new ScraperBinder();
	private Hashtable<Long, WebScraperUpdater> scrapers;
	private DatabaseWrapper dbWrapper;
	
	public static void start(Context context)
	{
		intent = new Intent(context,ScraperService.class);
		
		context.startService(intent);
	}
	
	public static void stop(Context context)
	{
		if (intent == null) return;
		
		context.stopService(intent);
	}
	
	public void refresh(long rowId)
	{
		scrapers.get(rowId).refresh();
	}
	
	public void update()
	{
		WebScraperUpdater scraper;
		
		//get all of the scrapers in the database
		Cursor cursor = dbWrapper.fetchAllScrapers();
		
		//for the current scrapers
		for(long rowId : scrapers.keySet())
		{
			//get the scraper
			scraper = scrapers.get(rowId);
			
			//update the scraper
			scraper.update();
			
			//if the scraper is deleted
			if (scraper.isDeleted())
			{
				//remove it from the list
				scrapers.remove(rowId);
			}
		}
		
		//for all the scrapers in the database
		while (cursor.moveToNext()) {
			//get the row id of the scraper
			long rowId = cursor.getLong(cursor.getColumnIndexOrThrow(WebScraper.KEY_ID));
			
			//if the list doesn't already contain the scraper
			if (!scrapers.containsKey(rowId))
			{			
				//create the scraper
				scraper = new WebScraperUpdater(context, rowId);
				//tell it to update
				scraper.update();
				//and put it in the list
				scrapers.put(rowId, scraper);
			}			
		}
		
		//close the table reference
		cursor.close();		
	}
	
	public void start()
	{
		for(WebScraperUpdater scraper : scrapers.values())
		{
			scraper.start();
		}
	}
	
	public void stop()
	{
		for(WebScraperUpdater scraper : scrapers.values())
		{
			scraper.stop();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return scraper_binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		dbWrapper = new DatabaseWrapper(context);
		scrapers = new Hashtable<Long, WebScraperUpdater>();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		update();
		start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		stop();
	}

	public static boolean isRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("infomaniac50.webscraper.service.ScraperService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	public class ScraperBinder extends Binder {
		public ScraperService getService() {
			return ScraperService.this;
		}
	}
}
