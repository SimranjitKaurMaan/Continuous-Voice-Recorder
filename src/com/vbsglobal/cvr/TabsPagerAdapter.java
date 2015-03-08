package com.vbsglobal.cvr;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		// TODO Auto-generated method stub
		switch (index) {
        case 0: Log.d("RecorderFragment", "tab1");
                 return new RecorderFragment();
        case 1:Log.d("PlayerFragment", "tab2");
        
               return new PlayerFragment();
		} 
		
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

}
