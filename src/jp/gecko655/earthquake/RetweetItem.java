package jp.gecko655.earthquake;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import twitter4j.TwitterException;
import twitter4j.Status;

public final class RetweetItem extends StatusItem{

    Status status;
    public RetweetItem(Context context, long statusId) {
        this.twitter = TwitterUtil.getTwitterInstance(context);
        this.context = context;
        getStatus(statusId);

    }
    synchronized private void getStatus(final long statusId){


        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    //twitter.updateStatus(listView.getText().toString());
                    twitter4j.Status status = twitter.showStatus(statusId);
                    return Arrays.asList(status);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    status = result.get(0);
                    content =status.getText();
                    MainActivity.PlaceholderFragment.updateListView();
                } else {
                    showToast("Something Wrong?:"+ context.getClass().getName());
                }
            }
        };
        task.execute();
    }

    @Override
    public String getScreenName() {
        if(status!=null){
            return "@"+status.getUser().getScreenName();
        }
        return "";
    }

    @Override
    public String getContent() {
        if(content!=null){
            return content;
        }
        return "";
    }

    @Override
    public void statusUpdate() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    if(status.isRetweetedByMe()){
                        Log.d("asdf","a");
                        twitter.destroyStatus(status.getCurrentUserRetweetId());
                    }
                        Log.d("asdf","b");
                    twitter4j.Status rtStatus = twitter.retweetStatus(status.getId());
                    return Arrays.asList(rtStatus);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    twitter4j.Status rtStatus =result.get(0);
                    showToast(rtStatus.getText() +" was Retweeted");
                    MainActivity.PlaceholderFragment.updateListView();
                } else {
                    showToast("Something Wrong?:"+ context.getClass().getName());
                }
            }
        };
        task.execute();
        getStatus(status.getId());
        
    }
    private void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
