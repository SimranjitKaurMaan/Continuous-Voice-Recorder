package com.vbsglobal.cvr;

import helper.CustomAdapter;
import helper.DatabaseHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import model.Model;
import model.Recordings;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
public class PlayerFragment extends  Fragment {
	private ImageButton playButton;
	private ImageButton backButton;
	private ImageButton forwardButton;
	private ImageButton pauseButton;
	private SeekBar ProgressBar;
	private TextView CurrentDurationLabel;
	private TextView TotalDurationLabel;
	private  View rootView;
	public static  List<Recordings> rList;
    private static ListView recordingsListView;
	Model m;
    static DatabaseHandler db;
    public  List<Recordings> descList;
    static Recordings[] recordingsItems;
    static Recordings[] recordingsArray;
	MediaPlayer mp;
	Handler seekHandler = new Handler();
	Runnable r;
	public ActionMode mActionMode;
	private static CustomAdapter adapter;
	private static List<Recordings> uList;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_player, container, false);
		Log.d("oncreateofplayer", "");
		initialiseViews();
		mp = new MediaPlayer();
		creatingListOfRecordings();
		setClickListeners();
		Log.d("oncreateofplayer", "");
        return rootView;
    }

	
	
	
	    public  void creatingListOfRecordings() {
	    	
	    	m= Model.getInstance(getActivity().getApplicationContext());
	    	db=m.getDatabaseObj(getActivity().getApplicationContext());
			Log.d("model object", ""+m);
			rList=db.getAllRec();
			Log.d("rList", ""+rList);
	    	int size=rList.size();
	    	Log.d("size", ""+size);
	    	descList=rList;
	    	Collections.reverse(descList);
	    	adapter = new CustomAdapter(getActivity().getApplicationContext(), descList);
	    	adapter.getContext(getActivity());
	    	Log.d("adapter", ""+adapter);
	    	recordingsListView.setAdapter(adapter);
	    	recordingsItems = new Recordings[size];
	    	Log.d("Items", ""+recordingsItems);
	    	String recordName,sizee,length,createdAt,record;
	    	int lastindex,i=0;
	    	
	    
	       	 for (Recordings r : rList) {
	    		Log.d("insideloop", "loop");
	    		recordName=r.getLoc();
	    		sizee=r.getSize();
	    		length=r.getLen();
	    		createdAt=r.getDateTime();
	    		lastindex=recordName.lastIndexOf("/");
	    		record=recordName.substring(lastindex+1);
	    		Log.d("record", ""+record);
	    		recordingsItems[i++] = new Recordings(record,length,createdAt,sizee,false);
		   		}
	    	recordingsArray=new Recordings[size];
	    	int j=0;
	    	for(i=size-1;i>=0;)
	    	{
	    		recordingsArray[i--]=recordingsItems[j++];
	    	}
	    	Log.d("exitloop", "loop");
	    	
	    	
	    }
	


	private void initialiseViews()
	{   Log.d("inside player", "views");
		recordingsListView = (ListView)rootView.findViewById(R.id.RecordingsListView);
		playButton=(ImageButton)rootView.findViewById(R.id.play);
		backButton=(ImageButton)rootView.findViewById(R.id.back);
		forwardButton=(ImageButton)rootView.findViewById(R.id.forward);
		pauseButton=(ImageButton)rootView.findViewById(R.id.pause);
		ProgressBar = (SeekBar)rootView.findViewById(R.id.seekbarplayer);
        CurrentDurationLabel = (TextView)rootView.findViewById(R.id.songCurrentDurationLabel);
        TotalDurationLabel = (TextView)rootView.findViewById(R.id.songTotalDurationLabel);
        
       
		
	}
	
	private void setClickListeners() 
	{
		Log.d("inside player", "setclicklisteners");
		playButton.setOnClickListener(onClickListener);
		backButton.setOnClickListener(onClickListener);
		forwardButton.setOnClickListener(onClickListener);
		recordingsListView.setOnItemClickListener(onItemClickListener);
		mp.setOnCompletionListener(onCompletionListener);
		ProgressBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		recordingsListView.setOnItemLongClickListener(onItemLongClickListener);
		
	}
	
	
	
	public OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> p, View v,
				int pos, long id) {
			// TODO Auto-generated method stub
			Log.d("pos item clicked", ""+pos);
			Log.d("id item clicked", ""+id);
			createCAB();
			v.setSelected(true);
		    return false;
		}

		public void createCAB() {
			mActionMode = getActivity().startActionMode(new CabHandler(getActivity()));
			}
		
 }; 
 
	
	
	
	private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
		          int progressChanged = 0;
		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean fromuser) {
			// TODO Auto-generated method stub
			      progressChanged = progress;
			      Log.d("progress", ""+progress);
			      
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekbar) {
			// TODO Auto-generated method stub
			Log.d("onStartTracking", "inside");
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekbar) {
			// TODO Auto-generated method stub
			Log.d("onStopTracking", "inside");
			
			
		}
		
	};
	
	
	 private OnClickListener onClickListener = new OnClickListener() {
	        private int seekForwardTime=5000;//ms
	        private int seekBackwardTime=5000;//ms


			@Override
	        public void onClick(View v) {
	            switch(v.getId()){
	                case R.id.play:playRecording();
	                     //DO something
	                break;
	                case R.id.back:fastBackward();
	                     //DO something
	                break;
	                case R.id.forward:fastForward();
	                     //DO something
	                break;
	               
	               
	                
	            }

	      }

			

			    private void fastBackward() {
				// get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mp.seekTo(0);
                }
 
				
			}

			private void fastForward() {
		        // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
			}
				
			

			private void playRecording() {
				// check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        playButton.setImageResource(R.drawable.playbutton);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        playButton.setImageResource(R.drawable.pausebutton);
                    }
                }
				
			}
	    };
	    
	   
	    private OnItemClickListener  onItemClickListener  = new OnItemClickListener() {
	        
			private int Index;

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				Log.d("inside player", "rowclicked");
				// getting listitem index
                Index = position;
                Log.d("inside player", "recording index : "+Index);
	            playButton.setImageResource(R.drawable.playbutton);
                startRecording();
                
             }

			private void startRecording() {
				mp.reset();
				Log.d("mp reset", ""+mp);
				String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder" + "/";
                path+=recordingsItems[Index].getLoc();
                Log.d("inside player", ""+path);
                // Changing Button Image to pause Image
				playButton.setImageResource(R.drawable.pausebutton);
                try {
					mp.setDataSource(path);
					Log.d("mp source", ""+mp);
					mp.prepare();
					Log.d("mp prepared", ""+mp);
					mp.start();
					Log.d("mp started", ""+mp);
					
					int tduration=mp.getDuration();
					ProgressBar.setMax(tduration);
					int seconds=((int)tduration/1000);
					String display;
					if (seconds < 60)
					    display = String.format("00:"+"%02d", seconds);
					else
					    display = String.format("0%d:%02d", seconds / 60, seconds % 60);
					TotalDurationLabel.setText(display);
			        seekUpdation();
					
					
					} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
				
			}

			
			private void seekUpdation() {
				Log.d("on seekupdation", ""+run);
	            ProgressBar.setProgress(mp.getCurrentPosition());
				seekHandler.postDelayed(run, 1000);
	            int cduration=mp.getCurrentPosition();
	            int seconds=(int)cduration/1000;
	            String display;
				if (seconds < 60)
				    display = String.format("00:"+"%02d", seconds);
				    
				else
				    display = String.format("0%d:"+"%02d", seconds / 60, seconds % 60);
				    
				   CurrentDurationLabel.setText(display);
				
				
			}
			
		     Runnable run = new Runnable() { 
				@Override public void run() {
					Log.d("inside runnable", ""+run);
					r=run;
					seekUpdation();
					}
				};

			
			
	    };
	
	    
	   
	    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d("onCompletion", "inside");
				playButton.setImageResource(R.drawable.playbutton);
				
				
			}

		  };
		
		public static void updateList() {
			Log.e("inside","updateList");
		      adapter.clear();
		      rList=db.getAllRec();
		       // Getting all Recordings in list
		 	   Log.e("Get Recordings", "Getting All Recordings");
		       for (Recordings r : rList) {
		 		Log.e("Recording loc",""+ r.getLoc());
		 		Log.e("Recording id", ""+r.getID());
		 		Log.e("Recording size",""+ r.getSize());
		 		Log.e("Recording length",""+r.getLen());
		 		Log.e("Recording created_at",""+r.getDateTime());
		 		//db.deleteRec(r);
		 		}
		      Collections.reverse(rList);
		      Log.e("Get Recordings", "Getting All Recordings");
		       for (Recordings r : rList) {
		 		Log.e("Recording loc",""+r.getLoc());
		 		Log.e("Recording id", ""+r.getID());
		 		Log.e("Recording size",""+ r.getSize());
		 		Log.e("Recording length",""+r.getLen());
		 		Log.e("Recording created_at",""+r.getDateTime());
		 		//db.deleteRec(r);
		 		}
			  adapter.addAll(rList);
			  recordingsListView.setAdapter(adapter);
			  adapter.notifyDataSetChanged();
			  int size=rList.size();
			recordingsItems = new Recordings[size];
		    	Log.d("Items", ""+recordingsItems);
		    	String recordName,sizee,length,createdAt,record;
		    	int lastindex,i=0;
		    	
		    
		       	 for (Recordings r : rList) {
		    		Log.d("insideloop", "loop");
		    		recordName=r.getLoc();
		    		sizee=r.getSize();
		    		length=r.getLen();
		    		createdAt=r.getDateTime();
		    		lastindex=recordName.lastIndexOf("/");
		    		record=recordName.substring(lastindex+1);
		    		Log.d("record", ""+record);
		    		recordingsItems[i++] = new Recordings(record,length,createdAt,sizee,false);
			   		}
		    	recordingsArray=new Recordings[size];
		    	int j=0;
		    	for(i=size-1;i>=0;)
		    	{
		    		recordingsArray[i--]=recordingsItems[j++];
		    	}
		    	Log.d("exitloop", "loop");
		    	
			  
		    
		  }  
		  
		  
		 @Override
	     public void onDestroy(){
	     super.onDestroy();
	     mp.release();
	     Log.d("on destroy", ""+r);
	     seekHandler.removeCallbacks(r);

	     }


	
	    

	 }
