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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
    final static String TAG = "EARTH_MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TwitterUtil.hasAccessToken(this)) {
            getAccessToken();
        }
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    private void getAccessToken() {
        TwitterUtil.deleteAccessToken(this);
        Intent intent = new Intent(this, TwitterOauthActivity.class);
        startActivity(intent);
        finish();
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
        if (id == R.id.action_auth) {
            getAccessToken();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        EditText editText;
        Twitter twitter;
        View rootView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            editText = (EditText) rootView.findViewById(R.id.twitter_tweet);
            twitter = TwitterUtil.getTwitterInstance(rootView.getContext());
            Button button = (Button) rootView.findViewById(R.id.button1);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
                        @Override
                        protected List<String> doInBackground(Void... params) {
                            try {
                                twitter.updateStatus(editText.getText().toString());
                                return null;
                            } catch (TwitterException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(List<String> result) {
                            if (result != null) {
                                Toast.makeText(rootView.getContext(), result.get(0), Toast.LENGTH_LONG).show();
                            } else {
                                Log.d(TAG,"Result was null");
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
