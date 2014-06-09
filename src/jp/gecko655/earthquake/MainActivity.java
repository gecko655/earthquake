package jp.gecko655.earthquake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ClipData.Item;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
    final static String TAG = "EARTH_MAIN";
    private Activity mActivity;
    
    public MainActivity(){
        mActivity = this;
    }

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
        ListView listView;
        Twitter twitter;
        View rootView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            twitter = TwitterUtil.getTwitterInstance(rootView.getContext());
            Button newTweetButton = (Button) rootView.findViewById(R.id.newTweetButton);
            Button newRetweetButton = (Button) rootView.findViewById(R.id.newRetweetButton);
            newRetweetButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    
                }
                
            });
            newTweetButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    
                }
                
            });
            listView = (ListView) rootView.findViewById(R.id.listView);
            StatusItemAdapter adapter = new StatusItemAdapter(rootView.getContext(),R.layout.status_item);
            adapter.add(new TweetItem(rootView.getContext(),"はらへ"));//For debug
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    ListView listView = (ListView)parent;
                    StatusItem item = (StatusItem)listView.getItemAtPosition(position);
                    item.statusUpdate();
                    
                }
                
            });
            return rootView;
        }
        private void showToast(String text) {
            Toast.makeText(rootView.getContext(), text, Toast.LENGTH_SHORT).show();
        }

    }

}
