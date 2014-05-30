package jp.gecko655.earthquake;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TwitterUtil.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOauthActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        TextView textView;
        Twitter twitter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            textView = (TextView) rootView.findViewById(R.id.twitter_tweet);
            twitter = TwitterUtil.getTwitterInstance(rootView.getContext());
            Button button = (Button) rootView.findViewById(R.id.button1);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
                        @Override
                        protected List<String> doInBackground(Void... params) {
                            try {
                                ResponseList<twitter4j.Status> timeline = twitter
                                        .getHomeTimeline();
                                List<String> list = new ArrayList<String>();
                                for (twitter4j.Status status : timeline) {
                                    list.add(status.getText());
                                }
                                return list;
                            } catch (TwitterException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(List<String> result) {
                            if (result != null) {
                                textView.setText(result.get(0));
                            } else {
                            }
                        }
                    };
                    task.execute();

                }

            });
            return rootView;
        }
    }

}
