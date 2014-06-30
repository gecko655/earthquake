package jp.gecko655.earthquake.realtime;

import jp.gecko655.earthquake.MainActivity;
import jp.gecko655.earthquake.TwitterUtil;
import twitter4j.FilterQuery;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Yurekuru {
	final String TAG = "Yurekuru";
	TwitterStream twitterStream;
	public Yurekuru(MainActivity mActivity){
		twitterStream = TwitterUtil.getTwitterStreamInstance(mActivity);
		final Twitter twitter = TwitterUtil.getTwitterInstance(mActivity);
		twitterStream.addListener(new YurekuruAdapter(mActivity));
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

}
