package jp.gecko655.earthquake;

import java.util.Arrays;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class TweetItem extends StatusItem {

    private String content;

    public TweetItem(long dbId, Context context, String content) {
        this.dbId=dbId;
        this.context = context;
        this.content = content;
        this.twitter = TwitterUtil.getTwitterInstance(context);
    }

    @Override
    public String getScreenName() {
        return "";
    }

    @Override
    public String getContent() {
        if (content != null) {
            return content;
        }
        return "";
    }

    @Override
    public void statusUpdate() {
        AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                try {
                    // twitter.updateStatus(listView.getText().toString());
                    twitter.updateStatus(getContent()
                            + System.currentTimeMillis());
                    return Arrays.asList("OK");
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<String> result) {
                if (result != null) {
                    showToast(result.get(0));
                } else {
                    showToast("Something Wrong?:"
                            + context.getClass().getName());
                }
            }
        };
        task.execute();

    }

    private void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
