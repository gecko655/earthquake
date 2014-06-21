package jp.gecko655.earthquake;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.AsyncTaskLoader;
import android.content.Context;

public class StatusLoader extends AsyncTaskLoader<twitter4j.Status> {

    private long statusId;
    private Context context;

    private StatusLoader(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public StatusLoader(Context context, long id) {
        super(context);
        this.context = context;
        this.statusId = id;

    }

    @Override
    public Status loadInBackground() {
        Twitter twitter = TwitterUtil.getTwitterInstance(context);
        try {
            twitter4j.Status status = twitter.showStatus(statusId);
            return status;
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

}
