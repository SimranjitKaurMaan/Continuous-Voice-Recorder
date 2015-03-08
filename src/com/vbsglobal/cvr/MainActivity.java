package com.vbsglobal.cvr;

import helper.CustomAdapter;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements TabListener {
	private static ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    
    public String folderName = "/ContinuousVoiceRecorder";
    // Tab titles
    private String[] tabs = { "RECORDER", "PLAYER" };
    public File outFile;
  

    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CabHandler ch=new CabHandler(getApplicationContext());
		Log.e("cabhander",""+ch);
		setContentView(R.layout.activity_main);
		addingTabs();
		setOnPageChangeListeners();
		settingExternalDirectory();
		
	}
	
	

	 private void settingExternalDirectory() {
	
		 String sdDirectory = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
		 System.out.println("sdDirectory"+sdDirectory);
	      outFile = new File(  sdDirectory + folderName);
	      if(!outFile.exists())
	      {
	      outFile.mkdir();
	      }   
		 Log.d("directory in MainActivity", "path :"+outFile);
		}

	private void setOnPageChangeListeners() {
		
		/**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
            	Log.d("position",""+position);
            	actionBar.setSelectedNavigationItem(position);
            	
                
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });	

		
		
	}

	private void addingTabs() {
		// Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.titlebar));
 
        viewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		// on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
        
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction arg1) {
		
		viewPager.startActionMode(new ActionMode.Callback() {
	        @Override
	        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	            // TODO Auto-generated method stub
	            return false;
	        }   
	        @Override
	        public void onDestroyActionMode(ActionMode mode) {
	            // TODO Auto-generated method stub

	        }   
	        @Override
	        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	            // TODO Auto-generated method stub
	            return false;
	        }   
	        @Override
	        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	            // TODO Auto-generated method stub
	            return false;
	        }
			
	    });
		
	}
	
	public static void switchToFragmentRecorder(){
        viewPager.setCurrentItem(0);
     }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        AlertDialog.Builder alertbox = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
	        alertbox.setIcon(R.drawable.ic_action_warning);
	        alertbox.setTitle("Do You Want To Exit??");
	        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) { 
	               // finish used for destroyed activity
	                finish();
	            }
	        });

	        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {
	                    // Nothing will be happened when clicked on no button 
	                    // of Dialog     
	          }
	        });

	        alertbox.show();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	
}
