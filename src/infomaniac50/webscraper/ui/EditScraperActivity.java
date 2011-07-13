package infomaniac50.webscraper.ui;

import infomaniac50.webscraper.R;
import infomaniac50.webscraper.storage.DatabaseWrapper;
import infomaniac50.webscraper.storage.WebScraper;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class EditScraperActivity extends Activity {
	protected final int SUCCESS_RETURN_CODE = 1;
	int[] positionList; 
	Long rowId;
    DatabaseWrapper dbWrapper;
    
    EditText txtName;
    EditText txtURL;
    EditText txtExpression;
    Spinner cmbInterval;
    boolean isNew;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_scraper);
        //Load preference data from XML
        Bundle b = this.getIntent().getExtras();
        positionList = getResources().getIntArray(R.array.cmbInterval_values);
        
        dbWrapper = new DatabaseWrapper(this);
        txtName = (EditText)findViewById(R.id.txtName);
        txtURL = (EditText)findViewById(R.id.txtURL);
        txtExpression = (EditText)findViewById(R.id.txtExpression);
        cmbInterval = (Spinner)findViewById(R.id.cmbInterval);
        
        isNew = b.getBoolean("new");
        rowId = null;
        if(!isNew)
        {
        	rowId = b.getLong(WebScraper.KEY_ID);
        	
        	populateFields();
        }
                    	
    }
    
    private void populateFields()
    {
    	if (rowId != null)
    	{
    		Cursor scraper = dbWrapper.fetchScraper(rowId);
    		if (!scraper.moveToFirst()) return;
    		
    		//startManagingCursor(scraper);
    		String Name = scraper.getString(scraper.getColumnIndexOrThrow(WebScraper.KEY_NAME));
    		String URL = scraper.getString(scraper.getColumnIndexOrThrow(WebScraper.KEY_URL));
    		String Expression = scraper.getString(scraper.getColumnIndexOrThrow(WebScraper.KEY_EXPRESSION));
    		int Interval = scraper.getInt(scraper.getColumnIndexOrThrow(WebScraper.KEY_INTERVAL));

    		txtName.setText(Name);
    		txtURL.setText(URL);
    		txtExpression.setText(Expression);
    		cmbInterval.setSelection(findPositionFromInterval(Interval));
    	}
    }
    
    public void btnSaveScraper_click(View view)
    {
    	setResult(SUCCESS_RETURN_CODE);
    	save();
    	this.finish();
    }
    
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);		
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		saveState();
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		populateFields();
//	}

	private void save() {
		//get the values
		String Name = txtName.getText().toString();
		String URL = txtURL.getText().toString();
		String Expression = txtExpression.getText().toString();
		int Interval = positionList[cmbInterval.getSelectedItemPosition()];
		
		//if the user decides not to create a scraper by pressing the back button
		if (Name.trim().equals("") || URL.trim().equals("") || Expression.trim().equals(""))
		{
			//then don't save a blank scraper
			//just return
			return;
		}
		
		if (isNew) {
			long id = dbWrapper.createScraper(Name, URL, Expression, Interval);
			if (id > 0) {
				rowId = id;
			}
		} else {
			dbWrapper.updateScraper(rowId, Name, URL, Expression, Interval);
		}
	}
    
    private int findPositionFromInterval(int Interval)
    {
    	int i = 0;
    	for(int v : positionList)
    	{
    		if (Interval == v)
    		{
    			break;
    		}
    		i++;
    	}
    	
    	return i;
    }
}
