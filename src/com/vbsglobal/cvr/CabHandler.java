package com.vbsglobal.cvr;

import helper.CustomAdapter;
import helper.DatabaseHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.Collections;
import java.util.List;

import model.Model;
import model.Recordings;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;



public class CabHandler implements ActionMode.Callback {
	private Context context;
	Model m;
	MenuItem item;
	public String mergedName;
	private Menu menu;
	public static boolean merge=false;
	private  DatabaseHandler db;
	public List<String> MergeFiles;
	private int RECORDER_SAMPLERATE;
	private long size;
	private int sz;
	private String appendFname;
	public static int idChecked;
	public static String locChecked;
	public   List <String> locSelected = new ArrayList<String>();
	public   List <Integer> idSelected = new ArrayList<Integer>();
    public ListView list;
    public  ArrayList<String> mNameList = new ArrayList<String>();
    private List<String> nameList = new ArrayList<String>();
    private Dialog mbuilder;
	
	public CabHandler(Context context)
	{
		
	    this.context=context;
	}
	
	
		public CabHandler(List<Recordings> checkList) {
    	String loc,name;int id;
    	for(int i=0;i<checkList.size();i++){
    	loc=checkList.get(i).getLoc();
        locChecked=loc;
		locSelected.add(locChecked);
        id=checkList.get(i).getID();
        idChecked=id;
	    idSelected.add(idChecked);
	    name=checkList.get(i).getName();
	    mNameList.add(name);
        }
    	for(String p : locSelected){
			Log.e("locSelected",""+p);
		}
    	Log.e("Namesize",""+mNameList.size());
    	Log.e("Name",""+mNameList);
    	
	}
		

	@Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
       int id = item.getItemId();
    	//Log.d("item id",""+id);
    	 m = Model.getInstance(context);
    	 db=m.getDatabaseObj(context);
    	switch(id){
		       case R.id.action_share: //Log.i("share","it");
			                           shareRecordings();
			                           
		                               //Do something
			                            break;
		        case R.id.action_edit:editRecording();
		                              mode.finish();
		                              
			                          //Do something
			                              break;
		        case R.id.action_discard:deleteRecordings();
		                                  mode.finish();
		                                  Log.d("mode","finish executed");
			                              //Do something                   
			                              break;
		        case R.id.action_merge:Log.e("nsize",""+mNameList.size());
		                               Log.e("locsize",""+locSelected.size());
		                               Log.e("Name",""+mNameList);
		        	                   mergeRecordings();
		                               mode.finish();
		                               Log.i("mode","executed");
			                          //Do something
			                           break;
		       case R.id.action_new:appendRecordings();
		                            mode.finish();
			                       //Do something
			                        break;
		        case R.id.action_ringtone:setRingtone();
		                                  mode.finish();
			                              //Do something
			                              break;
		        
			    default:
		
		}
    	
    	//Log.d("again","wrong move");
    	CustomAdapter.t=0;
			return false;
		 
        
    }

   

	

	private void deleteRecordings() {
		
		int t=CustomAdapter.t;
		Log.e("t value",""+t);
		if(t==1)
		deleteAlert();
		else 
		{   
			deleteMultipleAlert();
			
		}


		
	}

	private void deleteMultipleAlert() {
		Log.d("inside","delete multiple");
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK);
		 alertDialog.setTitle("Delete Recordings?");
         alertDialog.setMessage("The selected recordings can't be recovered once deleted.");
         alertDialog.setIcon(R.drawable.ic_action_warning);
         alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          

			public void onClick(DialogInterface dialog,int which) {
            	Log.d("delete alert","ok");
            
            	deleteMutiple();
			}

			private void deleteMutiple() {
				
				Log.e("delete","multiple");
				Recordings r;
				List<Recordings>l=CustomAdapter.list;
				Collections.reverse(l);
				for(int i=0;i<l.size();i++){
					Log.d("check state",""+ CustomAdapter.list.get(i).getchecked());
					Log.i("checkstate of"+i,""+l.get(i).getchecked());
					Log.i(""+i,""+l.get(i).getLoc());
					Log.i(""+i,""+l.get(i).getID());
					if(l.get(i).getchecked()==true)
					{    
						
						File file=new File(l.get(i).getLoc());
			        	if(file.exists()){
			        	boolean deleted = file.delete();
			        	Log.d("deleted","true"+deleted);
			        	Log.e("id_deleted",""+l.get(i).getID());
			        	r = db.getRec(l.get(i).getID());
			        	db.deleteRec(r);
			            
				   		}
						
					}
					
				}
				update();

				
				
					
				
			}

			
        });
        
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // Write your code here to invoke NO event
            	Log.d("delete alert","cancel");
            	update();
            dialog.cancel();
            }
        });
       alertDialog.show();
		
	}

	private void deleteAlert() {
		Log.d("inside","deleteAlert");
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK);
		 
        // Setting Dialog Title
        alertDialog.setTitle("Delete Recording?");
         
         
        // Setting Dialog Message
        alertDialog.setMessage(locChecked.substring(locChecked.lastIndexOf("/")+1)+" can't be recovered once deleted.");
 
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_action_warning);
 
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            private Recordings r;

			public void onClick(DialogInterface dialog,int which) {
            	Log.d("delete alert","ok");
            // Write your code here to invoke YES event
            	
            	File file=new File(locChecked);
            	if(file.exists()){
            	boolean deleted = file.delete();
            	Log.d("deleted","true"+deleted);
            	r = db.getRec(idChecked);
            	db.deleteRec(r);
            	update();
            	}
            
            }
        });
 
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // Write your code here to invoke NO event
            	Log.d("delete alert","cancel");
                update();
            dialog.cancel();
            }
        });
        
     // Showing Alert Message
        alertDialog.show();

	}

	private void setRingtone() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder/"; 
		ringtoneAlert();
		if(path != null){

			File k = new File(path,locChecked.substring(locChecked.lastIndexOf("/")+1) );
            String recName=locChecked.substring(locChecked.lastIndexOf("/")+1);
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, recName);
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav");
			values.put(MediaStore.Audio.Media.ARTIST, "Some Artist");
			values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
			values.put(MediaStore.Audio.Media.IS_ALARM, false);
			values.put(MediaStore.Audio.Media.IS_MUSIC, false);

			//Insert it into the database
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
			context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + k.getAbsolutePath() + "\"", null);
			Uri newUri = context.getContentResolver().insert(uri, values);
            RingtoneManager.setActualDefaultRingtoneUri(context,RingtoneManager.TYPE_RINGTONE,newUri);
			Log.d("set as","ringtone");
           
		}
		
		
	}

	private void ringtoneAlert() {
		// TODO Auto-generated method stub
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK);
				 // Setting Dialog Message
		      alertDialog.setMessage("Set "+locChecked.substring(locChecked.lastIndexOf("/")+1)+" as a ringtone?");

		      // Setting Positive "Yes" Button
		      alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog,int which) {

		          // Write your code here to invoke YES event
		        	  Log.d("delete alert","ok");
		        	  update();
		          }
		      });

		      // Setting Negative "NO" Button
		      alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int which) {
		         
		        	  Log.d(" alert","cancel");
		          dialog.cancel();
		          update();
		          }
		      });

		      // Showing Alert Message
		      alertDialog.show();
		
	}

	private  void appendRecordings() {
	  //moving to recorder fragment
		MainActivity.switchToFragmentRecorder();
	    Recordings r = db.getRec(idChecked);
		appendFname=r.getName();
		Log.d("appndFn cabh",""+appendFname);
		MainActivity.switchToFragmentRecorder();
		RecorderFragment.getAppendtoFile(appendFname);
        update();

     }

	private void mergeRecordings() {
		Log.e("checksize",""+mNameList.size());
		mergeAlert1();
		Log.i("next","next"); 
		
	}

	private void mergeAlert1() 
	{
		Log.i("mergeAlert1","enters");
        mbuilder = new Dialog(context,R.style.myBackgroundStyle);
        mbuilder.setTitle("Merge Files ");
        mbuilder.setContentView(R.layout.activity_merge_listview);
        Log.i("mergeAlert1","valid");
        Log.e("Name",""+mNameList);
        Log.e("checksize",""+mNameList.size());
        Log.e("locsize",""+locSelected.size());
        Recordings r;
		List<Recordings>l=CustomAdapter.list;
		Collections.reverse(l);
		for(int i=0;i<l.size();i++){
			Log.d("check state",""+ CustomAdapter.list.get(i).getchecked());
			Log.i("checkstate of"+i,""+l.get(i).getchecked());
			Log.i(""+i,""+l.get(i).getLoc());
			Log.i(""+i,""+l.get(i).getID());
			if(l.get(i).getchecked()==true)
			{    
                 mNameList.add(l.get(i).getName());
			}	
		}
				
 
        StableArrayAdapter adapter = new StableArrayAdapter(context, R.layout.textview, mNameList);
        final DynamicListView listView = (DynamicListView)mbuilder.findViewById(R.id.listview);
        listView.setNameList(mNameList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        nameList=listView.getList();
        mbuilder.setCancelable(true);
        Button okButton=(Button)mbuilder.findViewById(R.id.OkButton);
        okButton.setOnClickListener(new View.OnClickListener() { 
            


			@Override
            public void onClick(View v) {
                 mbuilder.dismiss();
                 Log.i("ok","dismissed");
                 getMergeFileName();
                 update();
                 
            }

			private void getMergeFileName() {
				
				final AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK);
		        builder.setTitle("Merge");  
		        builder.setIcon(R.drawable.ic_action_merge);
				builder.setMessage("Filename for merged recording? ");
				
		          final LinearLayout ll= new LinearLayout(context);
			         Log.i("inside","mergewerge");
			         ll.setOrientation(1);
		          final EditText input = new EditText(context);
		          input.setHint("My merged recording.wav");
		          input.setTextColor(Color.WHITE);
		          ll.addView(input);
			      builder.setView(ll);
			      
			      builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			          
			        	private String length;
						
			        	

						@Override
			            public void onClick(DialogInterface dialog, int whichButton) {
							String st;
							dialog.cancel();
							Toast.makeText(context, "Merging not started...",Toast.LENGTH_LONG).show();
							MergeFiles = new ArrayList<String>();
							nameList=listView.getList();
			        		for(String s:nameList)
			        			Log.e("nameList files", s);
			        		
			        		for(int j=0;j<nameList.size();j++)
			   	            {  
			        			  if(nameList.get(j)!=null)
			        				{
			        				st=nameList.get(j);
			        				MergeFiles.add(st);
			        				}
			        			
			        		}
			        		
			   	            
			        		mergedName=input.getText().toString().trim();
			        		if(mergedName.endsWith(".wav")==false)
			        			mergedName+=".wav";
			        		Log.e("mergedname",""+mergedName);
			        		for(String s:MergeFiles)
			        			Log.e("merge files", s);
			        		merge=true;
			        		int s = MergeFiles.size();
			        		findlength(MergeFiles);
			        		Log.d("duration of file",""+ length);
			        		Log.d("size of files",""+ s);
			        		
			        		Log.i("dialog","cancelled");
			        		if(merge == true)
			        		{
			        			
			        			wavMerger();
			        			getMetaData(mergedName, sz,length);
			                 
			        		}
			                return;
				
					
						}



					           private void findlength(List<String> mergeFiles) {
							    Integer t = 0;int k=0;
								String l;
								List<Recordings> list=db.getAllRec();
								String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder/";
								for(int i=0;i<mergeFiles.size();i++){
									String s = path + mergeFiles.get(i);
									for(int j=0;j<list.size();j++){
									   if(s.equals(list.get(j).getLoc())==true){
										   l=list.get(j).getLen();
										  t += Integer.valueOf(l.substring(l.lastIndexOf(":")+1));
										  
										 
										}
									   if(t<60)
									   length="00:"+String.valueOf(t);
									   else
									   {
										 while(t>=60)
											 {t-=60;k++;}
										 if(t!=0)
										     length="0"+k+":"+String.valueOf(t);
										 else
											 length="0"+k+":"+String.valueOf(t)+"0";
									   }
								}
							}
								
								
							
							
						}
			      
			      
			      
			      });
			      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    		 
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	Log.i("call","update");
			            	update();
			            	Log.i("cancel","dismissed");
			            	
			            	 return;
			            }
			        });
			 
			        builder.show();
			        return ;
			    

			}


						private void getMetaData(String mergedName, long size,
								String length) {
							
							String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder" + "/" + mergedName;
							  System.out.println("file location:"+path);
							  System.out.println("duration of recording : "+length);
							  System.out.println("size in bytes : "+size);
							  String sizee="";
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
							  String CREATED_AT = Integer.toString(hour)+":"+mn+x+","+m+" "+Integer.toString(day);
							  System.out.println("created_at "+CREATED_AT);
							 RecorderFragment.addingMetadataToDb(path,length,CREATED_AT,sizee,context);
							 
							 
							
						}


						
							private long round(float d, int decimalPlace) {
								BigDecimal bd = new BigDecimal(Float.toString(d));
						        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
						        return bd.longValue();
								
							}


						
			
          });
        Button cancelButton=(Button)mbuilder.findViewById(R.id.CancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View v) {
                 mbuilder.dismiss();
                 update();
                 Log.i("cancel","dismissed");
                 

                 
            }
          });
        
        
        
        mbuilder.show();
    }
	
	
	

	
	
	
	
	

	private void wavMerger() {
		try {
			String[] selection=new String[MergeFiles.size()];
			int s = MergeFiles.size();
			Log.i("s",""+s);
			for(int i=0;i<selection.length;i++){
			Log.d("selection",""+MergeFiles.get(i));
			selection[i]=MergeFiles.get(i);
			Log.d("merge",""+ selection[i]);
			}
			
			            DataOutputStream amplifyOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder/"+ mergedName)));
			            DataInputStream[] mergeFilesStream = new DataInputStream[selection.length];
			            long[] sizes=new long[selection.length];
			            for(int i=0; i<selection.length; i++) {
			                File file = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContinuousVoiceRecorder/" +selection[i]);
			                sizes[i] = (file.length()-44)/2;
			                sz=(int)file.length()+4+28+8;
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
			        size =0;
			        try {
			            FileInputStream fileSize = new FileInputStream(Environment.getExternalStorageDirectory() + "/ContinuousVoiceRecorder/"+mergedName);
			            size = fileSize.getChannel().size();
			            fileSize.close();
			        } catch (FileNotFoundException e1) {
			            // TODO Auto-generated catch block
			            e1.printStackTrace();
			        } catch (IOException e) {
			            // TODO Auto-generated catch block
			            e.printStackTrace();
			        }


			        final int RECORDER_BPP = 16;

			        long datasize=size+36;
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
			         header[40] = (byte) (size & 0xff);
			         header[41] = (byte) ((size >> 8) & 0xff);
			         header[42] = (byte) ((size >> 16) & 0xff);
			         header[43] = (byte) ((size >> 24) & 0xff);
			       // out.write(header, 0, 44); 

			        try {
			             RandomAccessFile rFile = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/ContinuousVoiceRecorder/"+ mergedName, "rw");
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

	



	private void editRecording() {
		editAlert();
		
		
	}

	private void editAlert() {
		 AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK);
	        builder.setTitle("Recording Name ");
	        
	         // Use an EditText view to get user input.
	         final EditText input = new EditText(context);
	         input.setTextColor(Color.WHITE);
	         builder.setView(input);
	         builder.setIcon(R.drawable.ic_action_edit);
	        
	 
	        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	 
	            private Recordings r;
				private Model model;

				@Override
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	String value = input.getText().toString();
	                Log.d("editAlert", "recording name: " + value);
	                if(value.contains(".wav")!=true)
	                {
	                value+=".wav";
	                }
	                setFileName(value);
	                update();
	                return;
	            }

				private void setFileName(String value) {
					String location= locChecked;
					Log.e("setFilename", "idChecked:"+idChecked);
					Log.e("setFilename","location"+location);
				    String newName =value;
				    Recordings rec=db.getRec(idChecked);
					rec.setName(newName);
					int flag = db.updateRec(rec);
					Log.d("flag",""+flag);
					PlayerFragment.updateList();
					
				   }
			        
	        });
	 
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	 
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Log.d("editAlert", "cancelled ");
	            	update();
	                return;
	            }
	        });
	 
	        builder.show();
	    
		
	}

	 private void shareRecordings() {
		 int t=CustomAdapter.t;
			Log.e("t value",""+t);
			if(t==1)
			shareRecording();
			else 
			{   shareMultipleRecordings();
			
			}
			
		
		}
	
	
	private void shareMultipleRecordings() {
		Log.i("inside shares", "shares");
		ShareActionProvider mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.action_share).getActionProvider();
		Log.e("mShareActionProvider", ""+mShareActionProvider);
		if (mShareActionProvider != null) {
		mShareActionProvider.setShareIntent(getMultipleShareIntent());
	
		}
 
		
		
	}

	private Intent getMultipleShareIntent() {
		List<Recordings>l=CustomAdapter.list;
		String [] filesToSend=new String[100];int j=0;
		for(int i=0;i<l.size();i++){
			Log.i(""+i,""+l.get(i).getchecked());
			Log.i(""+i,""+l.get(i).getLoc());
			Log.i(""+i,""+l.get(i).getID());
			if(l.get(i).getchecked()==true)
			{    
				
				filesToSend[j++] = l.get(i).getLoc();
	        	Log.e("filetosend",""+l.get(i).getLoc());
				
			}
			
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("audio/wav");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recordings");
        ArrayList<Uri> files = new ArrayList<Uri>();
      
        /* List of the files you want to send */
        for(String path : filesToSend ) {
        	if(path!=null){
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        	}
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        Log.i("intent", ""+intent);
        
		return intent;
	}

	private void shareRecording() {
		Log.e("inside share", "share");
		ShareActionProvider mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.action_share).getActionProvider();
		Log.e("mShareActionProvider", ""+mShareActionProvider);
		if (mShareActionProvider != null) {
		mShareActionProvider.setShareIntent(getDefaultShareIntent());
		}
 
		
	}

	private Intent getDefaultShareIntent() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
        intent.setType("audio/wav");
        Log.i("locChecked",""+locChecked);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recording");
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(locChecked)));
        Log.i("intent", ""+intent);
        return intent;
		
	}

	@Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        mode.getMenuInflater().inflate(R.menu.menu_bar, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        	//do something

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
    	mode.setTitle("Continuous voice recorder");
        return false;
    }
    
public void update()
{
	locSelected = new ArrayList<String>();
	idSelected = new ArrayList<Integer>();
	nameList=new ArrayList<String>();
	CustomAdapter.t=0;
	Log.d("db object", ""+db);
	List<Recordings>rlist=db.getAllRec();
	 Log.d("going inside","loop");
	 for (Recordings r : rlist) {
		    r.setchecked(false);
		    String log = " Name: " + r.getName() + " ,Length: " + r.getLen()+",Check: "+ r.getchecked();
	        System.out.println(log);
	        
	 }
	PlayerFragment.updateList();
	
	}
}
