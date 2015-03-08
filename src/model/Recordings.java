package model;

public class Recordings {
	//private variables
    int id;
    String location;
    String name;
    String length;
    String created_at;
    String size;
    boolean checked;

	public Recordings() {
		// TODO Auto-generated constructor stub
	}
	public Recordings(String location,String name, String length, String created_at, String size){
        this.location = location;
        this.length = length;
        this.created_at = created_at;
        this.size = size;
        this.name=name;
    }
	// constructor
    public Recordings(int id, String location,String name, String length, String created_at, String size){
        this.id = id;
        this.location = location;
        this.length = length;
        this.created_at = created_at;
        this.size = size;
        this.name=name;
    }
    public Recordings(String location, String length, String created_at, String size,boolean checked){
        this.location = location;
        this.length = length;
        this.created_at = created_at;
        this.size = size;
        this.checked=checked;
        this.name=name;
    }
    
    // getting checked
    public boolean getchecked(){
        return this.checked;
    }
     
    // setting checked
    public void setchecked(boolean checked){
        this.checked = checked;
    }
    
    // getting ID
    public int getID(){
        return this.id;
    }
     
    // setting id
    public void setID(int id){
        this.id = id;
    }
    
    // getting location
    public String getName(){
        return this.name;
    }
     
    // setting location
    public void setName(String name){
        this.name = name;
    }
    
    // getting location
    public String getLoc(){
        return this.location;
    }
     
    // setting location
    public void setLoc(String loc){
        this.location = loc;
    }
    
    // getting length
    public String getLen(){
        return this.length;
    }
     
    // setting length
    public void setLen(String len){
        this.length = len;
    }
    
    // getting dateTime
    public String getDateTime(){
        return this.created_at;
    }
     
    // setting dateTime
    public void setDateTime(String dt){
        this.created_at = dt;
    }
    
    // getting size
    public String getSize(){
        return this.size;
    }
     
    // setting size
    public void setSize(String size){
        this.size = size;
    }

}



