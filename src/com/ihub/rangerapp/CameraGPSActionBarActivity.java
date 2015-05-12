package com.ihub.rangerapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.ihub.rangerapp.data.service.ShiftService;
import com.ihub.rangerapp.data.service.ShiftServiceImpl;
import com.ihub.rangerapp.location.Coordinate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CameraGPSActionBarActivity extends ActionBarActivity {

	ImageButton gpsBtn;
	ImageButton cameraBtn;
	
	ProgressBar progressBar;
	
	TextView gpsText;
	ImageView imageView;
	
	LocationManager locationManager;
	LocationListener locationListener;
	
	Location lastLocation;
	
	String cameraNewImageUrl = "";
	String imagePath;
		
	Integer mode = 1; //1 - Create, 2 - Edit, 3 - View
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent data = getIntent();
        mode = data.getIntExtra("mode", 1);
		
		initLocationManager();
	}
	
	private void initLocationManager() {
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {

				lastLocation = location;
				
				if(mode == 1) {
					if(gpsText != null) {
						gpsText.setText(locationToString(location));
					}
					
					if(progressBar != null)
						progressBar.setIndeterminate(false);
				}
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		};

		  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}
	//http://stackoverflow.com/questions/17519198/how-to-get-the-current-location-latitude-and-longitude-in-android
	//TODO
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(locationManager == null)
			initLocationManager();
		
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		new CheckHasOpenShiftApplicationTask().execute();
	}
	
	private class CheckHasOpenShiftApplicationTask extends AsyncTask<Void, Void, Void> {
    	
		@Override
		protected Void doInBackground(Void... params) {
			ShiftService service = new ShiftServiceImpl();
			
			if(!service.hasOpenShift()) {
				Intent intent = new Intent(CameraGPSActionBarActivity.this, StartShiftActivity.class);
				startActivityForResult(intent, 10);
			}
				
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
    }

	protected void initViews() {
		gpsBtn = (ImageButton) findViewById(R.id.gpsBtn);
		cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
		
		gpsText = (TextView) findViewById(R.id.gpsText);
		imageView = (ImageView) findViewById(R.id.imageView);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setIndeterminate(mode == 1);
		
		cameraBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showUploadPopup(v);
			}
		});
		
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				zoom();
			}
		});
		
		if(mode != 1) {
			cameraBtn.setEnabled(false);
			
			if(getIntent().hasExtra("imagePath")) {
				try {
					imagePath = getIntent().getStringExtra("imagePath");
					showImage(imagePath);
				} catch (Exception e) {}
			}
			
			if(getIntent().hasExtra("wp") && !TextUtils.isEmpty(getIntent().getStringExtra("wp"))) {
				gpsText.setText(getWP());
			}
		}
		
		if(mode != 1) {
			
			gpsBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String l = "";
					if(lastLocation != null)
						l = lastLocation.getLatitude() + "     " + lastLocation.getLatitude();
					
					Location lc  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				
					if(gpsText != null) {
						
						if(lc != null)
							gpsText.setText(locationToString(lc));
					}
					
				}
			});
		}
		
		
	}
	
	protected void zoom() {

		if(!TextUtils.isEmpty(CameraGPSActionBarActivity.this.imagePath)) {
			
			Intent intent = new Intent(CameraGPSActionBarActivity.this, PhotoActivity.class);
			intent.putExtra("path", imagePath);
			startActivity(intent);
		}
	}
	
	public String locationToString(Location location) {
		Coordinate c = new Coordinate(location.getLatitude(), location.getLongitude());
		return c.toString();
	}
	
	private Uri getOutputMediaFileUri(){
	      return Uri.fromFile(getOutputMediaFile());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			
			if(requestCode == 300) {
				
				this.imagePath = cameraNewImageUrl;
				showImage(imagePath);
				
			} else if (requestCode == 200) {
				
				Uri selectedimg = data.getData();
								
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

	            Cursor cursor = getContentResolver().query(
	            		selectedimg, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();
	            
	            this.imagePath = filePath;
	            
	            showImage(imagePath);
			}
		}
	}
	
	protected void showImage(final String path) {
		
		Bitmap myBitmap = null;
		
		try {
			myBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 48, 48);
		} catch (Exception e) {}
		
		if(myBitmap != null)
			imageView.setImageBitmap(myBitmap);
	}
	
	private File getOutputMediaFile() {
		
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "RangerApp");
	    
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }
	    
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		        "IMG_"+ timeStamp + ".jpg");
	    cameraNewImageUrl = mediaFile.getAbsolutePath();
	    
	    return mediaFile;
	}
	
	protected void showUploadPopup(View v) {
    	PopupMenu popup = new PopupMenu(this, v);
    	popup.getMenuInflater().inflate(R.menu.upload_popup, popup.getMenu());
    	
    	popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            
    		@Override
            public boolean onMenuItemClick(MenuItem item) {
            	
            	if(item.getItemId() == R.id.openCamera) {
            		
            		Uri uri = getOutputMediaFileUri();
            		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            		startActivityForResult(intent, 300);
            		
            	} else if(item.getItemId() == R.id.fromGallery) {
            		
            		Intent intent = new Intent();
            	    intent.setType("image/*");
            	    intent.setAction(Intent.ACTION_GET_CONTENT);

            	    startActivityForResult(Intent.createChooser(intent,
            	            getString(R.string.open_gallery)), 200);
            	    
            	}
            	
            	return true;
            }
        });
        popup.show();
	}
	
	public String getWP() {
		return "0,40";
	}
	
	protected void showSaveResult(Map<String, Object> result) {
		
		String status = result.get("status").toString();
		
		if("success".equals(status)) {
			
			Toast toast = Toast.makeText(this, mode == 1 ? R.string.record_save_success_msg : R.string.record_update_success_msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			
			finish();
			
		} else {
			
			Toast toast = Toast.makeText(this, result.get("message").toString(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			
		}
	}
	
	protected Boolean isValid() {
		
		Boolean isValid = true;
		
		if(TextUtils.isEmpty(imagePath)) {
			isValid = false;
			Toast toast = Toast.makeText(this, getString(R.string.validation_photo), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
		}
		
		return isValid;
	}
}