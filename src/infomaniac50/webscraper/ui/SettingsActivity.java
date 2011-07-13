package infomaniac50.webscraper.ui;

import infomaniac50.webscraper.R;
import infomaniac50.webscraper.service.ScraperService;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
		
		CheckBoxPreference serviceControl = (CheckBoxPreference)findPreference("serviceControl");
		
		serviceControl.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (((CheckBoxPreference)preference).isChecked())
				{
					ScraperService.start(SettingsActivity.this);
				}
				else
				{
					ScraperService.stop(SettingsActivity.this);
				}
				return true;
			}
		});
	}
}
