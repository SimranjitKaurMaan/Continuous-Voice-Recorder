package helper;

import java.util.ArrayList;
import java.util.List;
import com.vbsglobal.cvr.CabHandler;
import com.vbsglobal.cvr.MainActivity;
import com.vbsglobal.cvr.PlayerFragment;
import com.vbsglobal.cvr.R;
import model.Model;
import model.Recordings;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Recordings> {
	Recordings[] recordingItems = null;
	public static List<Recordings> list;
    private LayoutInflater inflater;
    Model m;
	private DatabaseHandler db;
	int length;
	private Context context;
	public Context c;
	public static ActionMode mActionMode=null;
	private ActionMode ActionM;
	public static int  t;
	private int id;
	public int  getPosition;
	

	 public CustomAdapter(Context context, List<Recordings> list) {
		 super(context,R.layout.row_item,list);
		 inflater = LayoutInflater.from(context);
		 this.context = context;
		 this.list=list;
	}
	 
	 public void getContext(Context c){
		 this.c=c;
		 
	 }
	 
	 
	 
	
	 
	public static class ViewHolder {
	        protected TextView name;
	        protected TextView size;
	        protected TextView duration;
	        protected TextView createdAt;
			protected CheckBox cb;
	        
	    }
	 
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
     
	 ViewHolder viewHolder = null;
	 if (convertView == null) {
         convertView = inflater.inflate(R.layout.row_item,null); 
         viewHolder = new ViewHolder();
         viewHolder.name = (TextView) convertView.findViewById(R.id.RecordingName);
         viewHolder.createdAt = (TextView) convertView.findViewById(R.id.timeDate);
         viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
         viewHolder.size = (TextView) convertView.findViewById(R.id.size);
         viewHolder.cb = (CheckBox) convertView.findViewById(R.id.checkBox);
         
         viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             
             
             
			@Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				
                 getPosition = (Integer) buttonView.getTag(); // Here we get the position that we have set for the checkbox using setTag.
                 
                 Log.i("ischecked",""+isChecked);
                 list.get(getPosition).setchecked(buttonView.isChecked());// Set the value of checkbox to maintain its state.
                 CreateActionMode(buttonView,isChecked);
                
             }

			private void CreateActionMode(CompoundButton buttonView,boolean isChecked) {
				ArrayList<Recordings> checkList;
				checkList = getCheckedList();
				
				
				
				 if(checkList.size()!=0)
                 { 
                	  mActionMode= buttonView.startActionMode(new CabHandler(c));
                	  Log.i("mActionMode",""+mActionMode);
                      Log.i("inside","ischecked");
                      t=checkList.size();
                      Log.d("t",""+t);
                      CabHandler c= new CabHandler(checkList);
                            
                  } 
				 else
					 {
					 Log.e("he","llo");
					 Log.i("mActnMo",""+mActionMode);
					  if(mActionMode!=null)
	               	      mActionMode.finish();
					  else
					  {
						  mActionMode= buttonView.startActionMode(new CabHandler(c));
						  mActionMode.finish();
					  }
					 }
				
				
				
			}
         });
         convertView.setTag(viewHolder);
         convertView.setTag(R.id.RecordingName, viewHolder.name);
         convertView.setTag(R.id.timeDate, viewHolder.createdAt);
         convertView.setTag(R.id.size, viewHolder.size);
         convertView.setTag(R.id.checkBox, viewHolder.cb);
	 }else
	 {
		 viewHolder = (ViewHolder) convertView.getTag();
	 }
         
	 viewHolder.cb.setTag(position); // This line is important.
	 viewHolder.name.setText(list.get(position).getName());
     viewHolder.size.setText(list.get(position).getSize());
     viewHolder.duration.setText(list.get(position).getLen());
     viewHolder.createdAt.setText(list.get(position).getDateTime());
     viewHolder.cb.setChecked(list.get(position).getchecked());
     return convertView;
	 }

	 ArrayList<Recordings> getCheckedList() {
		 ArrayList<Recordings> checkList = new ArrayList<Recordings>();
			for (Recordings p : list) {
				if (p.getchecked())
					checkList.add(p);
			}
			 for(Recordings p : checkList)
			Log.i("checklist",""+ p.getName());
			return checkList;
		}
	
	 
	 
}