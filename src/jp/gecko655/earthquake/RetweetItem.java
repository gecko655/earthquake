package jp.gecko655.earthquake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Status;

public final class RetweetItem extends StatusItem {

    String screenName;
    long statusId;
    public RetweetItem(Context context, long statusId) {
        this.twitter = TwitterUtil.getTwitterInstance(context);
        this.context = context;
        this.statusId = statusId;
        getStatus();

    }
    private void getStatus(){
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    //twitter.updateStatus(listView.getText().toString());
                    twitter4j.Status status = twitter.showStatus(statusId);
                    List<twitter4j.Status> list =new ArrayList<twitter4j.Status>();
                    list.add(status);
                    return list;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    //showToast(context,result.get(0).getText());//for debug
                    content =result.get(0).getText();
                    screenName = result.get(0).getUser().getScreenName();
                    MainActivity.PlaceholderFragment.updateListView();
                } else {
                    showToast(context,"Something Wrong?:"+ context.getClass().getName());
                }
            }
        };
        task.execute();
    }

    @Override
    public String getScreenName() {
        return screenName;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void statusUpdate() {
        // TODO Auto-generated method stub
        
    }
    private void showToast(Context context,String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
