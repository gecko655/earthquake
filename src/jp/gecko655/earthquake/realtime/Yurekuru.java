package jp.gecko655.earthquake.realtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gecko655.earthquake.MainActivity;
import jp.gecko655.earthquake.R;
import jp.gecko655.earthquake.TwitterUtil;
import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Yurekuru extends Service{
	final String TAG = "Yurekuru";
	final String yurekuruUserName = "Yurekuru";
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
                    users = twitter.searchUsers(yurekuruUserName, 1);
                    FilterQuery filter = new FilterQuery();
                    long[] userIds = {users.get(0).getId()};
                    Log.d(TAG,""+userIds[0]);
                    filter.follow(userIds);
                    twitterStream.filter(filter);
                } catch (TwitterException e) {
                	Log.d(TAG,e.getMessage());
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
		Log.d(TAG,"onDestroy");
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
		final String MAGNITUDE ="マグニチュード";
        final String SEQ = "SEQ";
		

		/**
		 * @Override
		 * Parsing @yurekuru's tweet and compute whether the earthquake can be realized by the user or not.
		 */
		public void onStatus(Status status){
			Log.d(TAG,"Tweeted");
			String text = status.getText();
			double magnitude =getMagnitude(text);
			double distance = calcDistance(text);
			if(distance<0||magnitude<0){
				return;
			}
			Log.d(TAG,"distance="+distance);
			Log.d(TAG,"magnitude="+magnitude);
			if(isFirstSeq(text)&&isFeltEarthquake(distance,magnitude)){
				doNotify(text);
	            showToast("Earthquake happens near here!");
			}
		}



		private void doNotify(String text) {
            Intent intent = new Intent(getBaseContext(),MainActivity.class);;
            PendingIntent pi = PendingIntent.getActivity(Yurekuru.this, 0, intent, 0);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setContentIntent(pi);
            builder.setTicker("Earthquake happens near here!!");
            builder.setLargeIcon(icon);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setContentTitle("Earthquake");
            builder.setContentText("Earthquake happens near here!!");
            builder.setWhen(System.currentTimeMillis());
            builder.setDefaults(Notification.DEFAULT_ALL);
            NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            manager.notify(0,builder.build());

            Intent i = new Intent(Yurekuru.this,CallDialogActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Yurekuru.this.startActivity(i); 
		}


		private void showToast(String msg) {
			Log.d(TAG,msg);
			Toast.makeText(Yurekuru.this, msg, Toast.LENGTH_SHORT).show();
		}

		/**
		 * Calculate whether the earthquake is felt earthquake or not.
		 * "Ichikawa's maximum distance formula" is used. 
		 * @see <a href="http://www.jma.go.jp/jma/kishou/books/kenshin/vol25p083.pdf">http://www.jma.go.jp/jma/kishou/books/kenshin/vol25p083.pdf</a>
		 * @param distance
		 * @param magnitude
		 * @return
		 */
		private boolean isFeltEarthquake(double distance, double magnitude) {
			//Ichikawa's original formula is (magnitude=2.7*Math.log(distance/1000)-1.0) 
			//with the accuracy of M+-0.5.
			//To raise recall (not precision), magnitude is added by 0.5.
			return ((magnitude+0.5)>(2.7*Math.log10(distance/1000)-1.0));
		}

		/**
		 * 
		 * @param tweet
		 * @return magnitude or -1.0 if matcher failed
		 */
		private double getMagnitude(String tweet) {
			return getValue(MAGNITUDE,tweet);
		}

		/**
		 * 
		 * @param tweet
		 * @return latitude or -1.0 if matcher failed
		 */
		private double getLat(String tweet){
			return getValue(LATITUDE,tweet);
		}
		/**
		 * 
		 * @param tweet
		 * @return longitude or -1.0 if matcher failed
		 */
		private double getLng(String tweet){
			return getValue(LONGITUDE,tweet);
		}
		private boolean isFirstSeq(String tweet){
			return (getValue(SEQ,tweet)==1.0);
		}

		private double getValue(String param, String tweet) {
	        Pattern pattern = Pattern.compile(param + "：([0-9.]*)");
	        Matcher matcher=pattern.matcher(tweet);
	        if(matcher.find()){
	        	try{
                    return Double.parseDouble(matcher.group(1));
	        	}catch(NumberFormatException e){
	        		return -1.0;
	        	}
	        }
	        return -1.0;

		}

		private double calcDistance(String tweet) {
			double lat1 = getLat(tweet);
			double lng1 = getLng(tweet);
			Location location = getMyLocation();
            double lat2 = location.getLatitude();
            double lng2 = location.getLongitude();
            Log.d(TAG,"Current location > lat: "+lat2+ " lng "+lng2);
			if(lat1<0||lng1<0){
				return -1.0;
			}
	        double degToRad = Math.PI/180.0;
	        double latRad1 = lat1*degToRad;
	        double lngRad1 = lng1*degToRad;
	        double latRad2 = lat2*degToRad;
	        double lngRad2 = lng2*degToRad;
	        
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



		private Location getMyLocation() {
            LocationManager lm = (LocationManager) Yurekuru.this.getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null)
                location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(location==null){
            	Log.d(TAG,"location is null");
            }
            Log.d(TAG,location.toString());
            return location;
			
		}
		
	}



}
