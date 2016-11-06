package com.cyclebikeapp.plus1;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static com.cyclebikeapp.plus1.Constants.KEY_FORCE_NEW_TCX;
import static com.cyclebikeapp.plus1.Constants.KEY_TRAINER_MODE;
import static com.cyclebikeapp.plus1.Constants.KEY_VELO_CHOICE;
import static com.cyclebikeapp.plus1.Constants.PREFS_NAME;

public class TrainerModeSettings extends AppCompatActivity {
	private CheckBox trainerModeCheck;
	private Integer veloChoice;
	private ListView veloList;
	private static final String BTN_CHECK_ON = "10";
	private static final String BTN_CHECK_OFF = "11";
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_THUMB = "icon_type";
	private ArrayList<VelodromeSpec> velodromeList = new ArrayList<>();
	protected TMAdapter veloAdapter;
	private boolean forceNewTCX = false;
	private boolean scrolling = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (MainActivity.debugAppState) Log.i(this.getClass().getName() + " Trainer Mode", "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trainer_mode_view);
		setupActionBar();
		getWidgetIDs();
		veloList.setOnScrollListener(scrollListener);
		loadPreferences();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
			saveState();
	        NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(0, 0);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			// Show the Up button in the action bar.
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(R.string.trainer_mode_settings);
			actionBar.show();
		}
	}
	private void loadPreferences() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		trainerModeCheck.setChecked(settings.getBoolean(KEY_TRAINER_MODE, false));
		veloChoice = settings.getInt(KEY_VELO_CHOICE, 0);
		parseXML();// get velodromeList
		refreshVeloList();
		//set selected list item to veloChoice
		veloList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int topItem = veloList.getFirstVisiblePosition();
				veloChoice = position;
				refreshVeloList();
				veloList.setSelection(topItem);
				forceNewTCX = true;
			}			
		});
	}

	// this operates the turn-list scroller
	public OnScrollListener scrollListener = new OnScrollListener() {
		public void onScroll(AbsListView view, int firstItem,
				int visibleItemCount, int totalItemCount) {
		}
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (scrolling) {
					veloList.setSelection(veloList.getFirstVisiblePosition());
					scrolling = false;
				}
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			case OnScrollListener.SCROLL_STATE_FLING:
				scrolling = true;
				break;
			default:
				break;
			}
		}
	};

	private void refreshVeloList() {
		ArrayList<HashMap<String, String>> veloNamesList = new ArrayList<>();
		int veloNum = 0;
		for (VelodromeSpec tempVelo : velodromeList) {
			HashMap<String, String> map = new HashMap<>();
			map.put(KEY_FILENAME, tempVelo.getVelodromeName());
			//imageLevel refers to a checked, or unchecked checkbox icon (BTN_CHECK_ON:BTN_CHECK_OFF)
			//in image_icon.xml level-list; also use this in the file chooser
			String imageLevel = ((veloNum == veloChoice)?BTN_CHECK_ON:BTN_CHECK_OFF);
			
			map.put(KEY_THUMB, imageLevel);
			veloNamesList.add(map);
			veloNum++;
		}
		veloAdapter = new TMAdapter(this, veloNamesList);
		veloList.setAdapter(veloAdapter);
		veloList.setSelection(veloChoice);
	}

	private void getWidgetIDs() {
		trainerModeCheck = (CheckBox) findViewById(R.id.trainer_mode_checkbox);
		trainerModeCheck.setOnClickListener(trainerModeOnClick);
		veloList = (ListView) findViewById(R.id.velo_list);
		veloList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	private OnClickListener trainerModeOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
		}
	};		
	
	@Override
	protected void onPause() {
		saveState();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	private void saveState() {
		boolean trainerMode = trainerModeCheck.isChecked();
		if (trainerMode){
			forceNewTCX = true;
		}
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(KEY_TRAINER_MODE, trainerMode);
		editor.putBoolean(KEY_FORCE_NEW_TCX, forceNewTCX);
		editor.putInt(KEY_VELO_CHOICE, veloChoice);
		editor.apply();
	}
	
	private void parseXML() {
		AssetManager assetManager = this.getAssets();
		try {
			InputStream mInputStream = assetManager.open("velodromeList.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			VeloXMLParser myXMLHandler = new VeloXMLParser();
			xr.setContentHandler(myXMLHandler);
			InputSource inStream = new InputSource(mInputStream);
			xr.parse(inStream);
			velodromeList = myXMLHandler.getVeloList();
			mInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//parse XML file of velodromes from file in asset folder
}
