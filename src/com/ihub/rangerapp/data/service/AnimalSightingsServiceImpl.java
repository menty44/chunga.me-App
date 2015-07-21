package com.ihub.rangerapp.data.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.ihub.rangerapp.RangerApp;
import com.ihub.rangerapp.data.sqlite.Schemas;
import com.ihub.rangerapp.util.DateUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AnimalSightingsServiceImpl extends DatabaseService implements AnimalSightingsService {

	@Override
	public Map<String, Object> saveIndividualAnimal(Integer id, String name, String gender, String age, Integer distanceSeen, String extraNotes, String imagePath, String lat, String lon) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		SQLiteDatabase db = getWritableDatabase(RangerApp.get());
		
		ShiftService service = new ShiftServiceImpl();
		Long shiftID = service.getCurrentShiftID();
		
 		ContentValues values = new ContentValues();
 		if(id == -1)
 			values.put(Schemas.SHIFT_ID, shiftID);
 		
 		values.put(Schemas.IndividualAnimalSighting.ANIMAL, name);
 		values.put(Schemas.IndividualAnimalSighting.GENDER, gender);
 		values.put(Schemas.IndividualAnimalSighting.AGE, age);
 		values.put(Schemas.IndividualAnimalSighting.DISTANCE_SEEN, distanceSeen);
 		values.put(Schemas.IndividualAnimalSighting.EXTRA_NOTES, extraNotes);
 		values.put(Schemas.IndividualAnimalSighting.IMAGE_PATH, imagePath);
 		values.put(Schemas.IndividualAnimalSighting.LAT, lat);
 		values.put(Schemas.IndividualAnimalSighting.LON, lon);
 		
 		try {
 			
 			if(id == -1) {
 				db.insert(Schemas.INDIVIDUAL_ANIMAL_SIGHTING_TABLE, null, values);
 	 			result.put("status", "success");
 			} else {
 				db.update(Schemas.INDIVIDUAL_ANIMAL_SIGHTING_TABLE, values, BaseColumns._ID + "=" + id, null);
 				result.put("status", "success");
 			}
 			
 		} catch (Exception e) {
 			result.put("status", "error");
 			result.put("message", e.getMessage());
 		}
 		
		return result;
	}
	
	@Override
	public Map<String, Object> saveHerd(Integer id, String name, String species, Integer noOfAnimals, Integer adultsCount, Integer semiAdultsCount, Integer juvenileCount, Integer distanceSeen, String extraNotes, String imagePath, String lat, String lon) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		SQLiteDatabase db = getWritableDatabase(RangerApp.get());
		
		ShiftService service = new ShiftServiceImpl();
		Long shiftID = service.getCurrentShiftID();
		
 		ContentValues values = new ContentValues();
 		if(id == -1)
 			values.put(Schemas.SHIFT_ID, shiftID);
 		
 		values.put(Schemas.AnimalHerdSighting.NAME, name);
 		values.put(Schemas.AnimalHerdSighting.TYPE, species);
 		values.put(Schemas.AnimalHerdSighting.NUMBER_OF_ANIMALS, noOfAnimals);
 		
 		values.put(Schemas.AnimalHerdSighting.ADULTS_COUNT, adultsCount);
 		values.put(Schemas.AnimalHerdSighting.SEMI_ADULTS_COUNT, semiAdultsCount);
 		values.put(Schemas.AnimalHerdSighting.JUVENILE_COUNT, juvenileCount);
 		
 		values.put(Schemas.AnimalHerdSighting.DISTANCE_SEEN, distanceSeen);
 		values.put(Schemas.AnimalHerdSighting.EXTRA_NOTES, extraNotes);
 		values.put(Schemas.AnimalHerdSighting.IMAGE_PATH, imagePath);
 		values.put(Schemas.AnimalHerdSighting.LAT, lat);
 		values.put(Schemas.AnimalHerdSighting.LON, lon);
 		
 		try {
 			
 			if(id == -1) {
 				db.insert(Schemas.ANIMAL_HERD_SIGHTING_TABLE, null, values);
 	 			result.put("status", "success");
 			} else {
 				db.update(Schemas.ANIMAL_HERD_SIGHTING_TABLE, values, BaseColumns._ID + "=" + id, null);
 				result.put("status", "success");
 			}
 			
 		} catch (Exception e) {
 			result.put("status", "error");
 			result.put("message", e.getMessage());
 		}
 		
		return result;
	}
	
	@Override
	public void syncIndividual(Integer id, AsyncHttpResponseHandler handler) {
		
		String url = UrlUtils.INDIVIDUAL_ANIMALS_SIGHTING_URL;
		
		Cursor cursor = null;
		
		RequestParams params = new RequestParams();
		
		try {
			
        	SQLiteDatabase db = getWritableDatabase(RangerApp.get());
            cursor = db.rawQuery("SELECT * FROM " + Schemas.INDIVIDUAL_ANIMAL_SIGHTING_TABLE +" WHERE " + BaseColumns._ID +"=?", new String[] {id + ""});
            
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                
                params.put("device_record_id", cursor.getInt(0));
				String animal = cursor.getString(1);
				String gender = cursor.getString(2);
				String age = cursor.getString(3);
				Integer distanceSeen = cursor.getInt(4);
				
				String extraNotes = cursor.getString(5);
				String latitude = cursor.getString(6);
				String longitude = cursor.getString(7);
				String imagePath = cursor.getString(8);
				String dateCreated = cursor.getString(9);
				Integer shiftID = cursor.getInt(10);
				
				params.put("gender", gender);
				params.put("age", age);
				params.put("distance_seen", distanceSeen);
				
				params.put("animal", animal);
				params.put("lat", latitude);
				params.put("lon", longitude);
				params.put("extra_notes", extraNotes);
				
				try {
					File myFile = new File(imagePath);
				    params.put("image", myFile);
				    
				} catch(FileNotFoundException e) {}
				
				                
				ShiftService service = new ShiftServiceImpl();
				params.put("shift_unique_record_id", service.getShiftUniqueRecordID(shiftID));
				
				try {
        			params.put("record_date_created", DateUtil.parse(dateCreated).getTime() + "");
        			params.put("unique_record_id", RangerApp.getUniqueDeviceID() + "-" + DateUtil.parse(dateCreated).getTime());
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
            }
        }finally {
            cursor.close();
        }
		
		AsyncHttpClient client = new AsyncHttpClient();
		
		client.post(url, params, handler);
		
	}

	@Override
	public void syncHerd(Integer id, AsyncHttpResponseHandler handler) {

		String url = UrlUtils.HERD_SIGHTING_URL;
		
		Cursor cursor = null;
		
		RequestParams params = new RequestParams();

		try{
        	
        	SQLiteDatabase db = getWritableDatabase(RangerApp.get());
            cursor = db.rawQuery("SELECT * FROM " + Schemas.ANIMAL_HERD_SIGHTING_TABLE +" WHERE " + BaseColumns._ID +"=?", new String[] {id + ""});
            
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                
                params.put("device_record_id", cursor.getInt(0));
                String name = cursor.getString(1);
				String type = cursor.getString(2);
				Integer noOfAnimals = cursor.getInt(3);
				
				Integer adultsCount = cursor.getInt(4);
				Integer semiAdultsCount = cursor.getInt(5);
				Integer juvenileCount = cursor.getInt(6);
				
				Integer distanceSeen = cursor.getInt(7);
				
				
				params.put("name", name);
				params.put("type", type);
				params.put("no_of_animals", noOfAnimals + "");
				params.put("adults_count", adultsCount + "");
				params.put("semi_adults_count", semiAdultsCount + "");
				params.put("juvenile_count", juvenileCount + "");
				params.put("distance_seen", distanceSeen + "");
				
				String extraNotes = cursor.getString(8);
				String latitude = cursor.getString(9);
				String longitude = cursor.getString(10);
				String imagePath = cursor.getString(11);
				String dateCreated = cursor.getString(12);
				Integer shiftID = cursor.getInt(13);
				
				
				params.put("lat", latitude);
				params.put("lon", longitude);
				params.put("extra_notes", extraNotes);
				
				try {
					File myFile = new File(imagePath);
				    params.put("image", myFile);
				    
				} catch(FileNotFoundException e) {}
				
				                
				ShiftService service = new ShiftServiceImpl();
				params.put("shift_unique_record_id", service.getShiftUniqueRecordID(shiftID));
				
				try {
        			params.put("record_date_created", DateUtil.parse(dateCreated).getTime() + "");
        			params.put("unique_record_id", RangerApp.getUniqueDeviceID() + "-" + DateUtil.parse(dateCreated).getTime());
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
            }
        }finally {
            cursor.close();
        }
		
		AsyncHttpClient client = new AsyncHttpClient();
		
		client.post(url, params, handler);
	}

}
