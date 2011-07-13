package infomaniac50.webscraper.common;

import infomaniac50.webscraper.R;
import infomaniac50.webscraper.storage.DatabaseWrapper;
import infomaniac50.webscraper.storage.DbAdapter;
import infomaniac50.webscraper.storage.WebScraper;
import infomaniac50.webscraper.util.NetworkUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class WebScraperUpdater implements IUpdatable,IStartable {
	private final NotificationManager notifier;
	private final Context context;
	private final long rowId;
	private String name;
	private String url;
	private String expression;
	private int interval;	
	private Pattern pattern;
	private Timer timer;
	private boolean isDeleted = false;
	private boolean isChanged = false;
	private ScraperTask task;
	private DatabaseWrapper dbWrapper;
	
	public WebScraperUpdater(Context context, long RowId)
	{
		this.notifier = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		this.context = context;
		this.rowId = RowId;
		this.dbWrapper = new DatabaseWrapper(context);
	}
	
	public void stop()
	{
		if (timer != null)
		{		
			timer.cancel();
			timer = null;
		}
	}
	
	public void refresh()
	{
		if (!isDeleted)
		{
			task.run();
		}
	}
	
	public void start()
	{
		if (!isDeleted)
		{
			timer = new Timer();
			task = new ScraperTask(context, this.name, this.url, this.pattern);
			timer.scheduleAtFixedRate(task, 0, ((long)this.interval) * 60L * 1000L);
		}
	}
	
	public void update()
	{
		if (!isDeleted)
		{
			Cursor cursor = dbWrapper.fetchScraper(rowId);
			if (cursor.moveToFirst())
			{
				String name = cursor.getString(cursor.getColumnIndexOrThrow(WebScraper.KEY_NAME));
				String url = cursor.getString(cursor.getColumnIndexOrThrow(WebScraper.KEY_URL));
				String expression = cursor.getString(cursor.getColumnIndexOrThrow(WebScraper.KEY_EXPRESSION));
				int interval = cursor.getInt(cursor.getColumnIndexOrThrow(WebScraper.KEY_INTERVAL));

				setName(name);
				setURL(url);
				setExpression(expression);
				setInterval(interval);

				cursor.close();
				
				if (isChanged)
				{
					isChanged = false;
					stop();
					start();
				}
			}
			else
			{
				this.isDeleted = true;
				stop();
			}

		}
	}
	
	public boolean isDeleted()
	{
		return this.isDeleted;
	}
	
	private void setName(String name)
	{
		if (!name.equals(this.name))
		{
			this.name = name;
			isChanged = true;
		}
	}
	
	private void setURL(String url)
	{
		if (!url.equals(this.url))
		{
			this.url = url;
			isChanged = true;
		}
	}
	
	private void setExpression(String expression)
	{
		if (!expression.equals(this.expression))
		{
			this.expression = expression;
			int flags = 0;
			flags = Pattern.DOTALL;
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			
			if (pref.getBoolean("expressionCaseInsensitive", true))
			{
				flags |= Pattern.CASE_INSENSITIVE;
			}
			
			this.pattern = Pattern.compile(this.expression, flags);
			isChanged = true;
		}
	}
	
	private void setInterval(int interval)
	{
		if (this.interval != interval)
		{
			this.interval = interval;
			isChanged = true;
		}
	}
	
	public long getRowId()
	{
		return rowId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getURL()
	{
		return url;
	}
	
	public String getExpression()
	{
		return expression;
	}
	
	public int getInterval()
	{
		return interval;
	}

	private class ScraperTask extends TimerTask {
		private String currentValue = null;
		private String name;
		private String url;
		private Pattern pattern;
		private Context context;

		public ScraperTask(Context context, String name, String url, Pattern pattern) {
			this.context = context;
			this.name = name;
			this.url = url;
			this.pattern = pattern;
		}
		
//		public WebScraperUpdater parent()
//		{
//			return WebScraperUpdater.this;
//		}

		@Override
		public void run() {
			Document doc;
			String newValue;
			
			if (!NetworkUtil.isOnline(context))
			{
				//TODO: Maybe alert the user that we could not connect to the Internet. Really we don't want to pester them that they have crappy service. So maybe alert them just once.
				Log.w(ScraperTask.class.toString(),"Is the Internet available? Canceling update.");
				return;
			}
			
			try {
				Connection conn;
				if (!url.startsWith("http"))
				{
					conn = Jsoup.connect("http:\\" + url);
				}
				else
				{
					conn = Jsoup.connect(url);
				}
				//connect to the server
				
				//tell the server we want full webpages
				conn.userAgent("Mozilla");
				//download the document
				doc = conn.get();
			} catch (IOException e) {
				Log.e(ScraperTask.class.toString(), "Document Fetch Failed", e);
				return;
			}
			
			Matcher m = pattern.matcher(doc.text());
			
			//if the matcher did not find anything just return
			if (!m.find())
			{
				//TODO: alert the user their expression did not match any text
				return;
			}

			try
			{
				//get the first capture
				newValue = m.group(1);
			}
			catch(IllegalStateException e)
			{
				Log.e(ScraperTask.class.toString(), "No Captures Found", e);
				//TODO: alert the user their expression did not have any captures
				return;
			}
			
			//if this is the first capture we got just update the current value and return
			if (currentValue == null) {
				currentValue = newValue;
				return;
			}

			//if the current value is not equal to the new value
			if (!currentValue.equals(newValue)) {
				//get the preferences
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
				//action view is like entering a url in the shell run dialog on a pc
				//for any uri mailto:, tel:, or other uri android will open the default program
				//and pass the uri to the program
				Intent i = new Intent(Intent.ACTION_VIEW);
				//set the url for our view action
				i.setData(Uri.parse(url));
				//make an intent for the future
				PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
				//make a notification with the app icon
				//and the default ticker text
				Notification notification = new Notification(R.drawable.icon, context.getText(R.string.scraperUpdateTicker_text), System.currentTimeMillis());

				//use an alert tone for the notification
				if (pref.getBoolean("soundOnUpdate", false))
					notification.defaults |= Notification.DEFAULT_SOUND;

				//use the vibrator for the notification
				if (pref.getBoolean("vibrateOnUpdate", false))
					notification.defaults |= Notification.DEFAULT_VIBRATE;

				//cancel the notification when the user clicks it
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				
				notification.setLatestEventInfo(context, name, newValue, pi);

				notifier.notify((int) rowId, notification);
				currentValue = newValue;
			}
		}
	}
}
