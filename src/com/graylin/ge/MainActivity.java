package com.graylin.ge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.Shell;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	public native int openDevice(String path);
	public native int closeDevice();
	public native String readDevice();
    public native String stringFromJNI();

	public static final boolean isDebug = true;

	Handler handler;
	
	public static final int MSG_UPDATEEDITTEXT = 9000;
	boolean isStopEvent;
	boolean isCMDRunning;
	String cmdString;
	String outputContent;
	
//	static final int devicesListLen = 20;
//	static int devListCount;
//	String[] devicesList= new String[devicesListLen];

	ArrayAdapter<String> adapter;
	File root;
	public static List<String> fileList = new ArrayList<String>();
	
	public static SharedPreferences sharedPrefs;
	public static SharedPreferences.Editor sharedPrefsEditor;
	boolean isReadRaw;
	boolean isParam_t;
	boolean isParam_l;
	String inputDevicePath;
	
	public static Shell shell;
	public static Command command;
	
	public AdView adView;
	
	String testString;
	 
	public void startCMD(){
		
		if (isDebug) {
			Log.e("gray", "MainActivity.java:startCMD, cmdString:" + cmdString);
		}
		Message m = new Message();
		m.what = MSG_UPDATEEDITTEXT;
		m.obj = cmdString+"\n"+"================================\n";
		handler.sendMessage(m);
		
		isCMDRunning = true;
		
		command = new Command(0, 0, cmdString)
		{
		        @Override
		        public void output(int id, String line)
		        {
		        	if (isDebug) {
						Log.e("gray", "MainActivity.java:output, id:" + id + ", output:" + line);
					}
		        	
		        	if (!isStopEvent) {
		        		Message m = new Message();
		        		m.what = MSG_UPDATEEDITTEXT;
		        		m.obj = line+"\n";
		        		handler.sendMessage(m);
					}
		        }

				@Override
				public void commandCompleted(int arg0, int arg1) {
					// TODO Auto-generated method stub
					if (isDebug) {
						Log.e("gray", "MainActivity.java:commandCompleted, arg0:"+ arg0 + ", arg1:" + arg1);
					}
					Message m = new Message();
		    		m.what = MSG_UPDATEEDITTEXT;
		    		m.obj = "commandCompleted\n"+"================================\n";
		    		handler.sendMessage(m);
		    		
					isCMDRunning = false;
				}

				@Override
				public void commandOutput(int arg0, String arg1) {
					// TODO Auto-generated method stub
					if (isDebug) {
						Log.e("gray", "MainActivity.java:commandOutput, arg0:" + arg0 + ", arg1:" + arg1);
					}
					Message m = new Message();
		    		m.what = MSG_UPDATEEDITTEXT;
		    		m.obj = "commandOutput, arg0:" + arg0 + ", arg1:" + arg1 + "\n";
		    		handler.sendMessage(m);
				}

				@Override
				public void commandTerminated(int arg0, String arg1) {
					// TODO Auto-generated method stub
					if (isDebug) {
						Log.e("gray", "MainActivity.java:commandTerminated, arg0:" + arg0 + ", arg1:" + arg1);
					}
					Message m = new Message();
	        		m.what = MSG_UPDATEEDITTEXT;
	        		m.obj = "commandTerminated, reason:" + arg1 + "\n"+"================================\n";
	        		handler.sendMessage(m);
	        		
					isCMDRunning = false;
					
				}
				
		};
		
		try {
			shell = RootTools.getShell(true);
			shell.add(command);
		} catch (IOException e2) {
			Log.e("gray", "MainActivity.java:onCreate, " + e2.toString());
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (TimeoutException e2) {
			Log.e("gray", "MainActivity.java:onCreate, " + e2.toString());
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (RootDeniedException e2) {
			Log.e("gray", "MainActivity.java:onCreate, " + e2.toString());
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		RootTools.debugMode = true;
		
		handler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_UPDATEEDITTEXT:
					setResultText((String)msg.obj);
					break;

				}
				super.handleMessage(msg);
			}
		};
		
		// get SharedPreferences instance
		if (sharedPrefs == null) {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		}
		if (sharedPrefsEditor == null) {
			sharedPrefsEditor = sharedPrefs.edit();  
		}
		
		isReadRaw = sharedPrefs.getBoolean("pref_read_raw", false);
		isParam_t = sharedPrefs.getBoolean("pref_tag_t", true);
		isParam_l = sharedPrefs.getBoolean("pref_tag_l", true);
		inputDevicePath = sharedPrefs.getString("pref_device_path", "");
		if (isDebug) {
			Log.e("gray", "MainActivity.java:onCreate, isReadRaw:" + isReadRaw);
			Log.e("gray", "MainActivity.java:onCreate, isParam_t:" + isParam_t);
			Log.e("gray", "MainActivity.java:onCreate, isParam_l:" + isParam_l);
			Log.e("gray", "MainActivity.java:onCreate, inputDevicePath:" + inputDevicePath);
		}
		
		Message mm = new Message();
		mm.what = MSG_UPDATEEDITTEXT;
		mm.obj = "This tool need root privilege & busybox install!"+"\n";
		handler.sendMessage(mm);
		
		if (RootTools.isBusyboxAvailable()) {
			if (isDebug) {
				Log.e("gray", "MainActivity.java:onCreate, " + "Busybox Available!");
			}
			Message m = new Message();
    		m.what = MSG_UPDATEEDITTEXT;
    		m.obj = "Busybox Available!"+"\n";
    		handler.sendMessage(m);
		    // busybox exists, do something
		} else {
			if (isDebug) {
				Log.e("gray", "MainActivity.java:onCreate, " + "Busybox not Available!");
			}
			Message m = new Message();
    		m.what = MSG_UPDATEEDITTEXT;
    		m.obj = "Busybox not Available!"+"\n";
    		handler.sendMessage(m);
		    // do something else
		}
		
		if (RootTools.isRootAvailable()) {
			if (isDebug) {
				Log.e("gray", "MainActivity.java:onCreate, " + "Root Available!");
			}
			Message m = new Message();
    		m.what = MSG_UPDATEEDITTEXT;
    		m.obj = "Root Available!"+"\n"+"================================\n";
    		handler.sendMessage(m);
		    // su exists, do something
		} else {
			if (isDebug) {
				Log.e("gray", "MainActivity.java:onCreate, " + "Root not Available!");
			}
			Message m = new Message();
    		m.what = MSG_UPDATEEDITTEXT;
    		m.obj = "Root not Available!"+"\n"+"This app will not work *.*\n"+"================================\n";
    		handler.sendMessage(m);
		    // do something else
		}
		
		// getevent
		final Button button_start = (Button) findViewById(R.id.button_start);
		button_start.setOnClickListener(new View.OnClickListener() {
			
            public void onClick(View v) {
            	
            	if (isReadRaw) {
            		
            		if (inputDevicePath.equalsIgnoreCase("all event") || inputDevicePath.equalsIgnoreCase("")) {
            			Message m = new Message();
    	        		m.what = MSG_UPDATEEDITTEXT;
    	        		m.obj = "In this mode, you should choose an input event"+"\n";
    	        		handler.sendMessage(m);
    	        		return;
					}
					
            		System.loadLibrary("GetEvent");
            		
            		isStopEvent = false;
            		
            		if(!isCMDRunning){
            			isCMDRunning = true;
            			
            			new Thread(new Runnable() {
            				
            				
            				public void run() {
            					
            					if (openDevice(inputDevicePath) != 0) {
            						Message m = new Message();
            						m.what = MSG_UPDATEEDITTEXT;
            						m.obj = "open "+inputDevicePath+" error"+"\n"+"================================\n";
            						handler.sendMessage(m);
            						
            						isStopEvent = true;
								} else {
									Message m = new Message();
									m.what = MSG_UPDATEEDITTEXT;
									m.obj = "Monitoring raw start"+"\n"+"================================\n";
									handler.sendMessage(m);
								}
            					
            					while (!isStopEvent) {
            						
            						Message m = new Message();
            						m.what = MSG_UPDATEEDITTEXT;
            						m.obj = readDevice()+"\n";
            						handler.sendMessage(m);
            						
            					}
            					
            					closeDevice();
            					Message m = new Message();
        						m.what = MSG_UPDATEEDITTEXT;
        						m.obj = "Monitoring raw stop"+"\n"+"================================\n";
        						handler.sendMessage(m);
        						
            					isCMDRunning = false;
            				}
            				
            			}).start();
            			
            		}
            		
				} else {
					
					isStopEvent = false;
					
					if (isCMDRunning) {
						if (isDebug) {
							Log.e("gray", "MainActivity.java:onClick, " + "CMD is still Running, please stop first!");
						}
						
						Message m = new Message();
						m.what = MSG_UPDATEEDITTEXT;
						m.obj = "Command is still Running, please stop first!"+"\n";
						handler.sendMessage(m);
						return;
					}
					
					cmdString = "getevent -q";
					
					if (isParam_t) {
						cmdString += "t";
					}
					if (isParam_l) {
						cmdString += "l";
					}
					
					if (!inputDevicePath.equalsIgnoreCase("all event")) {
						cmdString += " ";
						cmdString += inputDevicePath;
					}
					
					startCMD();
				}
            	
            }
        });
		
		// stop
		final Button button_stop = (Button) findViewById(R.id.button_stop);
		button_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	isStopEvent = true;
            	
            	if (!isReadRaw) {
            		
            		if (command!=null) {
            			command.terminate("user terminated");
            		}
            		
            		RootTools.killProcess("getevent");
            		
            		try {
            			RootTools.closeAllShells();
            		} catch (IOException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}
            	}
        		
            }
        });
				
		// clean
		final Button button_clean = (Button) findViewById(R.id.button_clean);
		button_clean.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	final TextView tv = (TextView) findViewById(R.id.tv_logContent);
            	tv.setText("");
            	
            	outputContent = "";
            }
        });
		
		// info
		final Button button_info = (Button) findViewById(R.id.button_info);
		button_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	isStopEvent = false;
            	
            	if (isCMDRunning) {
            		if (isDebug) {
						Log.e("gray", "MainActivity.java:onClick, " + "CMD is still Running, please stop first!");
					}
            		
            		Message m = new Message();
	        		m.what = MSG_UPDATEEDITTEXT;
	        		m.obj = "Command is still Running, please stop first!"+"\n";
	        		handler.sendMessage(m);
					return;
				}
            	
            	cmdString = "getevent -i";
            	
            	if (isParam_l) {
            		cmdString += "l";
				}
            	
            	if (!inputDevicePath.equalsIgnoreCase("all event")) {
            		cmdString += " ";
            		cmdString += inputDevicePath;
				}
            	
            	startCMD();
            }
        });
		
	    adView = new AdView(this);
	    adView.setAdSize(AdSize.SMART_BANNER);
	    adView.setAdUnitId("ca-app-pub-5561117272957358/2024313602");

	    // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    LinearLayout layout = (LinearLayout) findViewById(R.id.ADLayout);
	    layout.addView(adView);

	    // Start loading the ad in the background.
	    adView.loadAd(new AdRequest.Builder().build());
	}
	
	public void setResultText(String s){
		
		if (s == null) {
			return;
		}
		
		outputContent += s;
		
		final TextView tv = (TextView) findViewById(R.id.tv_logContent);
		tv.append(s);
		
		final ScrollView svResult = (ScrollView) findViewById(R.id.scrollView1); 
		svResult.post(new Runnable() {  
	        public void run() {  
	            svResult.fullScroll(ScrollView.FOCUS_DOWN);  
	        }  
		});  	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (isDebug) {
			Log.e("gray", "MainActivity.java: onCreateOptionsMenu");
		}
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		if (isDebug) {
			Log.e("gray", "MainActivity.java:onOptionsItemSelected, " + "");
		}
		
		switch (item.getItemId()) {
        
		case R.id.action_settings:
			if (isDebug) {
        		Log.e("gray", "MainActivity.java:onOptionsItemSelected, case R.id.action_settings");
        	}
			
			fileList.clear();
//			root = new File("/dev/input");
//			ListDir(root, null);
//			root = new File("/dev");
//			ListDir(root, "hid");
			
			ListDirByCommand("ls /dev/input/");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ListDirByCommand("ls /dev/hid*");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			fileList.add("all event");
			
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, 0);
            
            isStopEvent = true;
            if (command!=null) {
            	command.terminate("go to setting, command terminated!");
			}
            
            RootTools.killProcess("getevent");
    		
    		try {
				RootTools.closeAllShells();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            break;
            
		case R.id.action_output:
			
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "getevent output");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, outputContent);
			startActivity(Intent.createChooser(sharingIntent, "output via ..."));
			break;
		}
		
		return true;
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case 0:
			
			isReadRaw = sharedPrefs.getBoolean("pref_read_raw", true);
			isParam_t = sharedPrefs.getBoolean("pref_tag_t", true);
			isParam_l = sharedPrefs.getBoolean("pref_tag_l", true);
			inputDevicePath = sharedPrefs.getString("pref_device_path", "");
			
			if (isDebug) {
				Log.e("gray", "MainActivity.java:onCreate, isReadRaw:" + isReadRaw);
				Log.e("gray", "MainActivity.java:onCreate, isParam_t:" + isParam_t);
				Log.e("gray", "MainActivity.java:onCreate, isParam_l:" + isParam_l);
				Log.e("gray", "MainActivity.java:onCreate, inputDevicePath:" + inputDevicePath);
			}
			
			if (isReadRaw) {
				if (inputDevicePath.equalsIgnoreCase("all event")) {
					Message m = new Message();
	        		m.what = MSG_UPDATEEDITTEXT;
	        		m.obj = "In this mode, you should choose an input event"+"\n";
	        		handler.sendMessage(m);
				} else {
					Command command = new Command(0, "chmod 777 " +  inputDevicePath)
					{

						@Override
						public void commandCompleted(int arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void commandOutput(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void commandTerminated(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}
						
					};
					try {
						RootTools.getShell(true).add(command);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RootDeniedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					cmdString = "chmod 777 " +  inputDevicePath;
//					startCMD();
				}
			}
			
		}
	}

	
	public void ListDir(File f, String filter){
		
		try {
			
			File[] files = f.listFiles();
//			fileList.clear();
			for (File file : files){
				Log.e("gray", "MainActivity.java:ListDir, " + file.getPath());
				if (filter != null) {
			    	 if(file.getName().contains(filter) ){
						fileList.add(file.getPath());  
			    	 }
				} else {
					fileList.add(file.getPath());  
				}
			}
			
		} catch (Exception e) {
			Log.e("gray", "SettingsActivity.java:ListDir, Exception:" + e.toString());
		}
	}
	
	public void ListDirByCommand(final String cmdString){
		
		Command command = new Command(0, cmdString)
		{

			@Override
			public void commandCompleted(int arg0, int arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void commandOutput(int arg0, String arg1) {
				
				if (cmdString.equalsIgnoreCase("ls /dev/input/")) {
					fileList.add("/dev/input/" + arg1);
				} else if (cmdString.equalsIgnoreCase("ls /dev/hid*")) {
					if (!arg1.contains("No")) {
						fileList.add(arg1);
					}
				}
				// TODO Auto-generated method stub
				
			}

			@Override
			public void commandTerminated(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
		};
		try {
			RootTools.getShell(true).add(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RootDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void onDestroy() {
		
		if (isDebug) {
			Log.e("gray", "MainActivity.java: onDestroy");	
		}
		
		// Destroy the AdView.
	    if (adView != null) {
	      adView.destroy();
	    }
	    
        if (command!=null) {
        	command.terminate("app close, onDestroy");
		}
        
        RootTools.killProcess("getevent");
		
		try {
			RootTools.closeAllShells();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onDestroy();
	}
	
}
