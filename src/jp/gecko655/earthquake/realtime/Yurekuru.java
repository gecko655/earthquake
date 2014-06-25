package jp.gecko655.earthquake.realtime;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import jp.gecko655.earthquake.TwitterUtil;
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public class Yurekuru {
	final String TAG = "Yurekuru";
	TwitterStream twitterStream;
	Context context;
	public Yurekuru(Context context){
		this.context=context;
		twitterStream = TwitterUtil.getTwitterStreamInstance(context);
		final Twitter twitter = TwitterUtil.getTwitterInstance(context);
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

	public void disConnect() {
		if(twitterStream!=null){
            twitterStream.shutdown();
		}
	}
	public class YurekuruAdapter extends StatusAdapter{

		@Override
		public void onStatus(Status status){
			Intent intent = new Intent();
			intent.setClassName("jp.gecko655.earthquake", "jp.gecko655.earthquake.MainActivity");
			context.startActivity(intent);
		}
		
	}
}
