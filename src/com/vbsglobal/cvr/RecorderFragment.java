package com.vbsglobal.cvr;

import helper.DatabaseHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Model;
import model.Recordings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecorderFragment extends Fragment {
	
	private static final int WAV_HEADER_LENGTH = 44;
	public String filename;
	private AlertDialog dialog;
	private File outFile;
    private boolean isRecording=false;
    private SeekBar seekBar;
    private ImageButton toggleButton;
    private ImageButton stopButton;
	private ImageButton cancelButton;
	public static Chronometer myChronometer;
    private View rootView;
	private MediaRecorder mRecorder;
	private TextView mTextView;
    private Object newButtonImage;
	private SharedPreferences mPrefs;
	public int sampleRate = 44100;
	public static final int BITRATE_WAV = 32 * 1024 * 8; // bits/sec
	private AudioRecord recordInstance;
	private File userFile;
	FileOutputStream outStream;
	private RemainingTimeCalculator mRemainingTimeCalculator;
	private TextView mMemory;
	private int flag=0;
	int bufferSize;
	byte[] tempBuffer;
	private float size;
	private String CREATED_AT;
	private String sizee;
	public  String length;
	private String path;
	static DatabaseHandler db;
	public List<Recordings> allRecs;
	private Context ctx;
	static Model model;
	private static String name;
	private int RECORDER_SAMPLERATE;
	int i=0,j=0;
	static String appName;
	private static int append=0;
	long timeWhenStopped = 0;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_recorder, container, false);
        initialiseViews();
        calculatingRemainingTime();
        setClickListeners();
        creatingDialog();
        return rootView;
        }
	
	  public void getMetaData(String filename,float size,String length) {
          System.out.println("inside getmetadata()");
		  this.filename=filename;
		  this.size=size;
		  this.length=length;
		  path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder" + "/" + filename;
		  System.out.println("file location:"+path);
		  System.out.println("duration of recording : "+length);
		  System.out.println("size in bytes : "+size);
		  if(size>=1024)
			  {
			  size/=1024.0;
			  size=round(size,1);
			  sizee=Float.toString(size) + "kB";
			  }
		      
		  else if(size>=1048576)
			   {
			    size/=1048576.0;
			    size=round(size,1);
			    sizee=Float.toString(size) + "MB";
			   }
		  System.out.println("sizee "+sizee);
		  Calendar c = Calendar.getInstance(); 
		  int day = c.get(Calendar.DATE);
		  System.out.println("date "+day);
		  int month=c.get(Calendar.MONTH);
		  System.out.println("month "+month);
		  int hour = c.get(Calendar.HOUR);
		  if(hour==0)
			  hour=12;
		  System.out.println("hour "+hour);
		  int min = c.get(Calendar.MINUTE);
		  String mn=Integer.toString(min);
		  if(mn.length()==1)
			  mn="0"+mn;
		  System.out.println("minute "+min);
		  int am_pm = c.get(Calendar.AM_PM);
		  System.out.println("am_pm "+am_pm);
		  String x;String m="";
		  if(am_pm==0)
			  x="am";
			  else
				  x="pm";
		  switch(month)
		  {
		  case 0: m = "Jan";break;
		  case 1: m = "Feb";break;
		  case 2: m = "Mar";break;
		  case 3: m = "Apr";break;
		  case 4: m = "May";break;
		  case 5: m = "June";break;
		  case 6: m = "July";break;
		  case 7: m = "Aug";break;
		  case 8: m =  "Sept";break;
		  case 9: m =  "Oct";break;
		  case 10: m = "Nov";break;
		  case 11: m = "Dec";
		  }
		  CREATED_AT=Integer.toString(hour)+":"+mn+x+","+m+" "+Integer.toString(day);
		  System.out.println("created_at "+CREATED_AT);
		  ctx=getActivity().getApplicationContext();
		  Log.i("ctxmeta",""+ctx);
		  addingMetadataToDb(path,length,CREATED_AT,sizee,ctx);
      }

	public static void addingMetadataToDb(String path, String length, String CREATED_AT, String sizee,Context context) {
		Log.i("adding","metadata to db");
		model= Model.getInstance(context);
		Log.d("contextrecorder",""+context);
		Log.d("modelrecorder", ""+model);
		db=model.getDatabaseObj(context);
		Log.d("db object", ""+db);
	    //create an object of Recordings class
		name=path.substring(path.lastIndexOf("/")+1);
		Recordings rec=new Recordings(path,name,length,CREATED_AT,sizee);
		long rec_id = db.addRec(rec);
		System.out.println("recording id : "+rec_id);
		Log.d("Recordings Count", "Recordings Count: " + db.getAllRec().size());
		PlayerFragment.updateList();

		}

	
	

	private void creatingDialog() {
		// get a generic dialog ready for alerts
	    dialog = new AlertDialog.Builder(getActivity()).create();
	    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
	      @Override
	      public void onClick(DialogInterface dialogInterface, int which) {
	        dialogInterface.dismiss();
	      }
	    });
		
	}


     private void setClickListeners() 
	{
		// TODO Auto-generated method stub
		toggleButton.setOnClickListener(onClickListener);
		stopButton.setOnClickListener(onClickListener);
		cancelButton.setOnClickListener(onClickListener);
		
		
		
	}
	
	 private OnClickListener onClickListener = new OnClickListener() {
	        
    private Thread t;
	private String mergedName;
	private String displayName;
	private long lsize=0;
	private int merge=0;
	private Thread s;
	private int stop=0;
	private ArrayList<String> TempFiles;
	private String tmp;
	private int audio_seekbar_progress;
	private SeekBar seekBar;

			@Override
	        public void onClick(View button) {
	            switch(button.getId()){
	                case R.id.toggle: if(isRecording)
  						                 pauseRecording();
  					                     else{
  						                 if(flag==0)
  							                 {
  						                	 flag=1;
  							                 startRecording();
  							                 }
  						                     else
  						                    resumeRecording();
  						                 }
  				
	                                       
	                                	 //DO something
	                break;
	                case R.id.stop:stop=1;stopRecording();
	                    //DO something
	                break;
	                case R.id.cancel:cancelRecording();
	                     //DO something
	                break;
	                
	               
	            }

	      }
			
			
			  private void resumeRecording() {
			    System.out.println("inside resumeRecording()");
				isRecording = true;
				merge=1;
				toggleUi();
				checkSdcardPresence();
				    s=null;
				    s = new Thread(new SpaceCheck());
				    s.start();
				    filename="record_temp"+(i++)+".wav";
				    TempFiles.add(filename);
				    myChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
				   myChronometer.start();
				    t=null;
				    t = new Thread(new Capture());
				    t.start();
				    

	    }


			private void pauseRecording() {
				isRecording=false;
                toggleUi();
				System.out.println("inside pauseRecording()");
				timeWhenStopped = myChronometer.getBase() - SystemClock.elapsedRealtime();
				stopRecording();
			}

			private void stopRecording() {
				
				if(null != recordInstance){
	                isRecording = false;
	                toggleUi();
	                recordInstance.stop();
	                recordInstance.release();
	                recordInstance=null;
	              
	                myChronometer.stop();
	               
	               
	                t = null;
	                try {
						outStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
				if (outFile != null) {
			          appendHeader(outFile);

			       }
				
				
				if(append==1 && stop==1){
					length=myChronometer.getText().toString();
					appendFile();
					merge=2;
					
				}
				
			   if(merge==0 && stop==1)
				{
				   length=myChronometer.getText().toString();
				   getMetaData(filename,size,length);
				   
				}
				
				if(merge==1 && stop==1)
				{   length=myChronometer.getText().toString();
					 tmp="temprecord.wav";
					 Log.e("displayName",""+displayName);
					 Log.e("tmp",""+tmp);
					String sdDirectory = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/ContinuousVoiceRecorder";
					File from = new File(sdDirectory,displayName);
			        File to = new File(sdDirectory,tmp);
			        boolean n = from.renameTo(to);
			        Log.e("rename recording",""+n);
			        
				    File file = new File(sdDirectory+"/" + displayName);
				    if(file.exists())
				         file.delete();
					try {
						boolean created=file.createNewFile();
						Log.e("created",""+created+file);
						
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					
					 wavMerger(displayName);
					getMetaData(displayName,size,length);
				}
				if(stop==1)
				{
				myChronometer.setBase(SystemClock.elapsedRealtime());
				timeWhenStopped = 0;
				flag=0;
				append=0;
				merge=0;
				stop=0;
				mTextView.setVisibility(View.INVISIBLE);
				}
				
				
				
			}

			private void appendFile() {
				
				tmp="temprecord.wav";
				String sdDirectory = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/ContinuousVoiceRecorder";
				File from = new File(sdDirectory,appName);
		        File to = new File(sdDirectory,tmp);
		        
		        boolean n = from.renameTo(to);
		        Log.e("rename",""+n);
		       File file = new File(sdDirectory+"/" + appName);
			      if (file.exists())
			          file.delete();
				try {
					file.createNewFile();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				wavMerger(appName);
				getMetaData(appName,size,length);
			}


			public void wavMerger(String name) {
				try {
					TempFiles.add(0,tmp);
					String[] selection=new String[TempFiles.size()];
					int s = TempFiles.size();
					Log.i("s",""+s);
					for(int i=0;i<selection.length;i++){
					Log.d("selection",""+TempFiles.get(i));
					selection[i]=TempFiles.get(i);
					Log.d("merge",""+ selection[i]);
					}
				DataOutputStream amplifyOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder/"+ name)));
					            DataInputStream[] mergeFilesStream = new DataInputStream[selection.length];
					            long[] sizes=new long[selection.length];
					            for(int i=0; i<selection.length; i++) {
					                File file = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder/" +selection[i]);
					                sizes[i] = (file.length()-44)/2;
					                size += (int) file.length() + 4 + 28 + 8;
					            }
					            for(int i =0; i<selection.length; i++) {
					                mergeFilesStream[i] =new DataInputStream(new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory() + "/ContinuousVoiceRecorder/" +selection[i])));

					                if(i == selection.length-1) {
					                    mergeFilesStream[i].skip(24);
					                    byte[] sampleRt = new byte[4];
					                    mergeFilesStream[i].read(sampleRt);
					                    ByteBuffer bbInt = ByteBuffer.wrap(sampleRt).order(ByteOrder.LITTLE_ENDIAN);
					                    RECORDER_SAMPLERATE = bbInt.getInt();
					                    mergeFilesStream[i].skip(16);
					                }
					                    else {
					                        mergeFilesStream[i].skip(44);
					                    }

					            }

					            for(int b=0; b<selection.length; b++) {
					            for(int i=0; i<(int)sizes[b]; i++) {
					                 byte[] dataBytes = new byte[2];
					                 try {
					                 dataBytes[0] = mergeFilesStream[b].readByte();
					                 dataBytes[1] = mergeFilesStream[b].readByte();
					                 
					                 }
					                 catch (EOFException e) {
					                    amplifyOutputStream.close();
					                 }
					                 short dataInShort = ByteBuffer.wrap(dataBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
					                 float dataInFloat= (float) dataInShort/37268.0f;


					                short outputSample = (short)(dataInFloat * 37268.0f);
					                byte[] dataFin = new byte[2];
					               dataFin[0] = (byte) (outputSample & 0xff);
					               dataFin[1] = (byte)((outputSample >> 8) & 0xff);
					            
					              amplifyOutputStream.write(dataFin, 0 , 2);

					            }
					            }
					            amplifyOutputStream.close();
					            for(int i=0; i<selection.length; i++) {
					                mergeFilesStream[i].close();
					            }

					        } catch (FileNotFoundException e) {

					            e.printStackTrace();
					        } catch (IOException e) {

					            e.printStackTrace();
					        }
					        lsize =0;
					        try {
					            FileInputStream fileSize = new FileInputStream(Environment.getExternalStorageDirectory() + "/ContinuousVoiceRecorder/"+name);
					            lsize = fileSize.getChannel().size();
					            fileSize.close();
					        } catch (FileNotFoundException e1) {
					            // TODO Auto-generated catch block
					            e1.printStackTrace();
					        } catch (IOException e) {
					            // TODO Auto-generated catch block
					            e.printStackTrace();
					        }


					        final int RECORDER_BPP = 16;
                            
					        long datasize=lsize+36;
					        long byteRate = (RECORDER_BPP * RECORDER_SAMPLERATE)/8;
					        long longSampleRate = RECORDER_SAMPLERATE;
					        byte[] header = new byte[44];


					         header[0] = 'R';  // RIFF/WAVE header 
					         header[1] = 'I';
					         header[2] = 'F';
					         header[3] = 'F';
					         header[4] = (byte) (datasize & 0xff);
					         header[5] = (byte) ((datasize >> 8) & 0xff);
					         header[6] = (byte) ((datasize >> 16) & 0xff);
					         header[7] = (byte) ((datasize >> 24) & 0xff);
					         header[8] = 'W';
					         header[9] = 'A';
					         header[10] = 'V';
					         header[11] = 'E';
					         header[12] = 'f';  // 'fmt ' chunk
					         header[13] = 'm';
					         header[14] = 't';
					         header[15] = ' ';
					         header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
					         header[17] = 0;
					         header[18] = 0; 
					         header[19] = 0;
					         header[20] = 1;  // format = 1
					         header[21] = 0;
					         header[22] = (byte) 1;
					         header[23] = 0;
					         header[24] = (byte) (longSampleRate & 0xff);
					         header[25] = (byte) ((longSampleRate >> 8) & 0xff);
					         header[26] = (byte) ((longSampleRate >> 16) & 0xff);
					         header[27] = (byte) ((longSampleRate >> 24) & 0xff);
					         header[28] = (byte) (byteRate & 0xff);
					         header[29] = (byte) ((byteRate >> 8) & 0xff);
					         header[30] = (byte) ((byteRate >> 16) & 0xff);
					         header[31] = (byte) ((byteRate >> 24) & 0xff);
					         header[32] = (byte) ((RECORDER_BPP) / 8);  // block align
					         header[33] = 0;
					         header[34] = RECORDER_BPP;  // bits per sample
					         header[35] = 0;
					         header[36] = 'd';
					         header[37] = 'a';
					         header[38] = 't';
					         header[39] = 'a';
					         header[40] = (byte) (lsize & 0xff);
					         header[41] = (byte) ((lsize >> 8) & 0xff);
					         header[42] = (byte) ((lsize >> 16) & 0xff);
					         header[43] = (byte) ((lsize >> 24) & 0xff);
					       // out.write(header, 0, 44); 

					        try {
					             RandomAccessFile rFile = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/ContinuousVoiceRecorder/"+ name, "rw");
					            rFile.seek(0);
					            rFile.write(header);
					            rFile.close();
					        } catch (FileNotFoundException e) {
					            // TODO Auto-generated catch block
					            e.printStackTrace();
					        } catch (IOException e) {
					            // TODO Auto-generated catch block
					            e.printStackTrace();
					        }
				
				
					  
					        
				
			}


			private void cancelRecording() {
				if(null != recordInstance){
	                isRecording = false;
	                recordInstance.stop();
	                myChronometer.stop();
	               length=myChronometer.getText().toString();
	               myChronometer.setBase(SystemClock.elapsedRealtime());
	                t = null;
	                try {
						outStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
				deleteFile();
				
			}
			
			private void deleteFile() {
	            outFile.delete();
	     }

			
			
			
			
			
            public void startRecording() {
            	checkSdcardPresence();
            	if(append==0)
                {
                 generatingFileName();
                 }
            	TempFiles=null;
            	TempFiles = new ArrayList<String>();
            	if(append==1)
            		{
            		 filename="temp_append.wav";
            	   	 displayName=filename;
            		 mTextView.setVisibility(View.VISIBLE);
    				 mTextView.setText(appName);
    				 mTextView.setTextSize(25);
            		 TempFiles.add(appName);
            		}
            	TempFiles.add(filename);
            	
            	
            	
                isRecording = true;
				Thread s = new Thread(new SpaceCheck());
			    s.start();
			    toggleUi();
			    t = new Thread(new Capture());
			    t.start();
			    
			    System.out.println("timer starts counting up");
			    if(flag==1)
			    {
			    	Log.d("reset ","clock");
			         myChronometer.setBase(SystemClock.elapsedRealtime());
			    }
			    
			    if(append==1)
                {
			    	String lastlen = findLenOfLastRec();
			    	int stoppedMilliseconds = calcChrono(lastlen);
			    	myChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
                 }
			     myChronometer.start();
				
       }
           

			private int calcChrono(String lastlen) {
				int stoppedMilliseconds = 0;
				Log.i("lastlen",""+lastlen);
		        String array[] = length.split(":");
		        if (array.length == 2) {
		          stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000
		              + Integer.parseInt(array[1]) * 1000;
		        } else if (array.length == 3) {
		          stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 
		              + Integer.parseInt(array[1]) * 60 * 1000
		              + Integer.parseInt(array[2]) * 1000;
		        }

		        return stoppedMilliseconds;
				
			}


			private String findLenOfLastRec() {
				String lastlen = null;
				ctx=getActivity().getApplicationContext();
				model= Model.getInstance(ctx);
				Log.d("contextrecorder",""+ctx);
				Log.d("modelrecorder", ""+model);
				db=model.getDatabaseObj(ctx);
				Log.d("db object", ""+db);
				List<Recordings>rlist=db.getAllRec();
				 Log.d("going inside","loop");
				 for (Recordings r : rlist) {
					    String log = " Name: " + r.getName() + " ,Length: " + r.getLen();
				        System.out.println(log);
				        Log.d("appName",""+appName);
			            if(r.getName().equalsIgnoreCase(appName))
			            	{
			            	 Log.i("RecLength: ", r.getLen());
			            	 lastlen = r.getLen(); 
			            	 length=lastlen;
			            	 Log.e("lastlen(loop):",""+lastlen);
			            	 db.deleteRec(r);
			            	}
			            
			           
			             }
				return length;
				
		
			}


			private void checkSdcardPresence() {
				// check that there's somewhere to record to
			    String state = Environment.getExternalStorageState();
			    Log.d("FS State", state);
			     if (state.equals(Environment.MEDIA_REMOVED)) {
			      showDialog("Insert SD Card", "Please insert an SD card. You need something to record onto.");
			      System.exit(0);
			      }
			  
			      return;
				
			}

			private void generatingFileName() {
				// generating a file name each time it starts to record
			     mPrefs= getActivity().getPreferences(0);
				 int count = mPrefs.getInt("count",0);
				 Editor editor = mPrefs.edit();
				  count=count +1;
			     filename = "My recording #"+count+".wav";
			     displayName=filename;
			     Log.d("RecorderFragment","Count is "+ count);
				 editor.putInt("count",count);
				 editor.commit();
				 mTextView.setVisibility(View.VISIBLE);
				 mTextView.setText(filename);
				 mTextView.setTextSize(25);
                 userFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder" + "/" + filename);
                 Log.d("userfile", ""+userFile);
			     return;

			}
};
	
	
	
	
			private void showDialog(String title, String message){
			    dialog.setTitle(title);
			    dialog.setMessage(message);
			    dialog.show();
			  }
			
	
			/**
			* Toggles the UI images from record to pause and vice versa.
			*/
			private void toggleUi() { 
				toggleButton = (ImageButton)rootView.findViewById(R.id.toggle);
				Drawable newButtonImage;
				if (isRecording) {
				newButtonImage =
				getResources().getDrawable(R.drawable.pause11);
				} else {
				newButtonImage =
				getResources().getDrawable(R.drawable.record11);
				}
				toggleButton.setImageDrawable(newButtonImage);
				}


	private void initialiseViews()
	{     
		  mMemory=(TextView)rootView.findViewById(R.id.memoryText);
		  myChronometer = (Chronometer)rootView.findViewById(R.id.timer);
		  mTextView = (TextView)rootView.findViewById(R.id.fileName);
		  mTextView.setVisibility(View.INVISIBLE);
		  toggleButton = (ImageButton)rootView.findViewById(R.id.toggle);
		  stopButton = (ImageButton)rootView.findViewById(R.id.stop);
	      cancelButton = (ImageButton)rootView.findViewById(R.id.cancel);  
	      seekBar = (SeekBar)rootView.findViewById(R.id.seekBar1);
	      seekBar.setMax(4400);
		
		}
	
	private void calculatingRemainingTime()
	{
		mRemainingTimeCalculator = new RemainingTimeCalculator();
		mRemainingTimeCalculator.setBitRate(BITRATE_WAV);
		long seconds = mRemainingTimeCalculator.timeRemaining();
		Log.d("RemainingTime", "seconds"+seconds);
		float hours=(float)seconds/3600;
		String hrs=Float.toString(round(hours,1));
		mMemory.setText("Storage memory remaining : "+hrs+" hours");
		
	}
	
	/**
     * Round to certain number of decimals
     * 
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
	
	 /**
	   * Monitors the available SD card space while recording.
	   *  
	   */
	  private class SpaceCheck implements Runnable {
	    @Override
	    public void run() {
	      String sdDirectory = Environment.getExternalStorageDirectory().toString();
	      Log.d("sdDirectory in spaceCheck",""+sdDirectory);
	      StatFs stats = new StatFs(sdDirectory);
	      Log.d("stats in spaceCheck",""+stats);
	      while (isRecording) {
	        stats.restat(sdDirectory);
	        final long freeBytes = (long) stats.getAvailableBlocks() * (long) stats.getBlockSize();
	        Log.d("freebytes in spaceCheck",""+freeBytes);
	        if (freeBytes < 5242880) { // less than 5MB remaining
	          runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	              showDialog("Low on disk space", "There isn't enough space " + "left on your SD card (" + freeBytes
	                  + "b) , but what you've " + "recorded up to now has been saved.");
	              toggleButton.performClick();
	            }

			});
	          return;
	        }

	        try {
	          Thread.sleep(3000000);
	          Log.d("Not sleepy","");
	        } catch (InterruptedException e) {
	        }
	      }
	    }

		private void runOnUiThread(Runnable runnable) {
			// TODO Auto-generated method stub
			
		}

		
	  }
	  /**
	   * Capture raw audio data from the hardware and saves it to a buffer in the enclosing class. 
	   **/
	  private class Capture implements Runnable {

	    private final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
	    private final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		
	
	 

		// the actual output format is big-endian, signed

	    @Override
	    public void run() {
	      // We're important...
	      android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

	      // Allocate Recorder and Start Recording...
	       int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
	      Log.d("minbuffersize reqd for audiorecorder obj creation",""+minBufferSize);
	      if (AudioRecord.ERROR_BAD_VALUE == minBufferSize || AudioRecord.ERROR == minBufferSize){
	        runOnUiThread(new Runnable() {
	          @Override
	          public void run() {
	            showDialog("Error recording audio", "Your audio hardware doesn't support the sampling rate 44100.");
	            toggleButton.performClick();
	          }
	        });
	        return;
	      }
	      int bufferSize = 2 * minBufferSize;
	      Log.d("bufferSize",""+bufferSize);
	      recordInstance =
	          new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioEncoding,
	              bufferSize);
	      Log.d("recordInstance in Capture",""+recordInstance);
	      if (recordInstance.getState() != AudioRecord.STATE_INITIALIZED) {
	        runOnUiThread(new Runnable() {
	          @Override
	          public void run() {
	            showDialog("Error recording audio", "Unable to access the audio recording hardware - is your mic working?");
	            toggleButton.performClick();
	          }
	        });
	        return;
	      }
          
	      byte[] tempBuffer = new byte[bufferSize];
	      String sdDirectory = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	      outFile = new File(sdDirectory + "/ContinuousVoiceRecorder"+"/" + filename);
	      if (outFile.exists())
	        outFile.delete();
	        outStream = null;
	      try {
	        outFile.createNewFile();
	        outStream = new FileOutputStream(outFile);
	        Log.d("outstream", ""+outStream);
	        outStream.write(createHeader(0));// Write a dummy header for a file of length 0 to get updated later
	      } catch (Exception e) {
	        runOnUiThread(new Runnable() {
	          @Override
	          public void run() {
	            showDialog("Error creating file", "The WAV file you specified "
	                + "couldn't be created. Try again with a " + "different filename.");
	            outFile = null;
	            toggleButton.performClick();
	          }
	        });
	        return;
	      }

	      recordInstance.startRecording();

	      try {
	        while (isRecording) {
	        	recordInstance.read(tempBuffer, 0, bufferSize);
	            outStream.write(tempBuffer);
	        }
	      } catch (final IOException e) {
	        runOnUiThread(new Runnable() {

	          @Override
	          public void run() {
	            showDialog("IO Exception", "An exception occured when writing to disk or reading from the microphone\n"
	                    + e.getLocalizedMessage()
	                    + "\nWhat you have recorded so far should be saved to disk.");
	            toggleButton.performClick();
	          }

	        });
	      } catch (OutOfMemoryError om) {
	        runOnUiThread(new Runnable() {
	          @Override
	          public void run() {
	            showDialog("Out of memory", "The system has been " + "too strong for too long - but what you "
	                + "recorded up to now has been saved.");
	            System.gc();
	            toggleButton.performClick();
	          }
	        });
	      }

	     
	    }

		private void runOnUiThread(Runnable runnable) {
			// TODO Auto-generated method stub
			
		}
		
}
	  /**
	   * Appends a WAV header to a file containing raw audio data. Uses different strategies depending
	   * on amount of free disk space.
	   * 
	   * @param file The file containing 16-bit little-endian PCM data.
	   */
	  public void appendHeader(File file) {

	    int bytesLength = (int) file.length();
	    byte[] header = createHeader(bytesLength - WAV_HEADER_LENGTH);

	    try {
	      RandomAccessFile ramFile = new RandomAccessFile(file, "rw");
	      ramFile.seek(0);
	      ramFile.write(header);
	      ramFile.close();
	    } catch (FileNotFoundException e) {
	      Log.e("cvr", "Tried to append header to invalid file: " + e.getLocalizedMessage());
	      return;
	    } catch (IOException e) {
	      Log.e("cvr", "IO Error during header append: " + e.getLocalizedMessage());
	      return;
	    }

	  }
	  
	  /**
	   * Creates a valid WAV header for the given bytes, using the class-wide sample rate
	   * 
	   * @param bytes The sound data to be appraised
	   * @return The header, ready to be written to a file
	   */
	  public byte[] createHeader(int bytesLength) {
		Log.i("createHeader", ""+bytesLength);  
        int totalLength = bytesLength + 4 + 24 + 8;
        size = totalLength;
	    byte[] lengthData = intToBytes(totalLength);
	    byte[] samplesLength = intToBytes(bytesLength);
	    byte[] sampleRateBytes = intToBytes(this.sampleRate);
	    byte[] bytesPerSecond = intToBytes(this.sampleRate * 2);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    try {
	      out.write(new byte[] {'R', 'I', 'F', 'F'});
	      out.write(lengthData);
	      out.write(new byte[] {'W', 'A', 'V', 'E'});

	      out.write(new byte[] {'f', 'm', 't', ' '});
	      out.write(new byte[] {0x10, 0x00, 0x00, 0x00}); // 16 bit chunks
	      out.write(new byte[] {0x01, 0x00, 0x01, 0x00}); // mono
	      out.write(sampleRateBytes); // sampling rate
	      out.write(bytesPerSecond); // bytes per second
	      out.write(new byte[] {0x02, 0x00, 0x10, 0x00}); // 2 bytes per sample

	      out.write(new byte[] {'d', 'a', 't', 'a'});
	      out.write(samplesLength);
	    } catch (IOException e) {
	      Log.e("Create WAV", e.getMessage());
	    }

	    return out.toByteArray();
	  }

	  /**
	   * Turns an integer into its little-endian four-byte representation
	   * 
	   * @param in The integer to be converted
	   * @return The bytes representing this integer
	   */
	  public static byte[] intToBytes(int in) {
	    byte[] bytes = new byte[4];
	    for (int i = 0; i < 4; i++) {
	      bytes[i] = (byte) ((in >>> i * 8) & 0xFF);
	    }
	    return bytes;
	  }
	  
	  
	  
	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    isRecording = false;
	    System.out.println("OnDestroy");
	    if(recordInstance!=null)
	    recordInstance.release();
	    
	  }

	public static void getAppendtoFile(String appendFname) {
	   appName=appendFname;
	   append=1;
	  }

	
}