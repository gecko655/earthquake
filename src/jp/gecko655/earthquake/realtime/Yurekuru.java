package jp.gecko655.earthquake.realtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gecko655.earthquake.MainActivity;
import jp.gecko655.earthquake.TwitterUtil;
import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Yurekuru extends Service{
	final String TAG = "Yurekuru";
	TwitterStream twitterStream;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate(){
		Log.d(TAG,"onCreate");
		twitterStream = TwitterUtil.getTwitterStreamInstance(this);
		final Twitter twitter = TwitterUtil.getTwitterInstance(this);
		twitterStream.addListener(new YurekuruAdapter());
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
                try {
                    ResponseList<User> users;
                    users = twitter.searchUsers("gecko535", 1);
                    FilterQuery filter = new FilterQuery();
                    long[] userIds = {users.get(0).getId()};
                    Log.d(TAG,""+userIds[0]);
                    filter.follow(userIds);
                    twitterStream.filter(filter);
                } catch (TwitterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				return null;
			}
        };
        task.execute();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		disConnect();
	}


	public void disConnect() {
		if(twitterStream!=null){
            twitterStream.shutdown();
		}
	}
	
	public class YurekuruAdapter extends StatusAdapter{
		final String TAG = "YurekuruAdapter";
		final String LATITUDE ="緯度";
		final String LONGITUDE ="経度";
		

		@Override
		public void onStatus(Status status){
			double lat=getLat(status.getText());
			double lng=getLng(status.getText());
			if(lat<0||lng<0){
				return;
			}
			double distance = calcDistance(lat,lng);
			Log.d(TAG,"distance="+distance);
			if(distance<100*1000.0){//distance is less than 100 km
				if(!MainActivity.isRunning()){
	                Intent intent = new Intent(getBaseContext(),MainActivity.class);;
	                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                getApplication().startActivity(intent);
				}else{
				}
	            showToast("Earthquake happens near here!");
			}
		}


		private void showToast(String msg) {
			Log.d(TAG,msg);
			Toast.makeText(Yurekuru.this, msg, Toast.LENGTH_SHORT).show();
		}


		/**
		 * 
		 * @param tweet
		 * @return latitude of -1.0 if matcher failed
		 */
		public double getLat(String tweet){
			return getLatLng(LATITUDE,tweet);
		}
		/**
		 * 
		 * @param tweet
		 * @return longitude of -1.0 if matcher failed
		 */
		public double getLng(String tweet){
			return getLatLng(LONGITUDE,tweet);
		}

		private double getLatLng(String axisName, String tweet) {
	        Pattern pattern = Pattern.compile(axisName + "：([0-9.]*)");
	        Matcher matcher=pattern.matcher(tweet);
	        if(matcher.find()){
	            return Double.parseDouble(matcher.group(1));
	        }
	        return -1.0;

		}

		private double calcDistance(double lat, double lng) {
	        double degToRad = Math.PI/180.0;
	        double latRad1 = lat*degToRad;
	        double lngRad1 = lng*degToRad;
	        double latRad2 = 35.6*degToRad;
	        double lngRad2 = 139.4*degToRad;
	        
	        double latAve = (latRad1+latRad2)/2;
	        double latDiff = (latRad1-latRad2);
	        double lngDiff = (lngRad1-lngRad2);
	        //子午線曲率半径
	        //半径を6335439m、離心率を0.006694で設定してます
	        double meridian = 6335439 / Math.sqrt(Math.pow(1 - 0.006694 * Math.sin(latAve) * Math.sin(latAve), 3));
	        //卯酉線曲率半径
	        //半径を6378137m、離心率を0.006694で設定してます
	        double primevertical = 6378137 / Math.sqrt(1 - 0.006694 * Math.sin(latAve) * Math.sin(latAve));

	        //Hubenyの簡易式
	        double x = meridian * latDiff;
	        double y = primevertical * Math.cos(latAve) * lngDiff;

	        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
		}
		
	}



}
