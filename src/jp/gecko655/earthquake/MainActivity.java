package jp.gecko655.earthquake;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import jp.gecko655.earthquake.db.DBAdapter;
import jp.gecko655.earthquake.db.DatabaseOpenHelper;
import twitter4j.Twitter;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
    final static String TAG = "EARTH_MAIN";

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!TwitterUtil.hasAccessToken(this)) {
            getAccessToken();
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        //Create admob
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
            /* Database Initialize */
            DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(
                    this.getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            db.close();
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
        private static View rootView;
        private static StatusItemAdapter adapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            if(adapter==null){
                adapter = new StatusItemAdapter(rootView.getContext(),
                        R.layout.status_item);
                initAdapter();
            }
            listView.setAdapter(adapter);

            /* listeners */
            Button newTweetButton = (Button) rootView
                    .findViewById(R.id.newTweetButton);
            Button newRetweetButton = (Button) rootView
                    .findViewById(R.id.newRetweetButton);
            Button reloadButton = (Button) rootView
                    .findViewById(R.id.action_reload);
            reloadButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    initAdapter();
                }
            });
            newRetweetButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new NewRTFragment())
                            .addToBackStack(null)
                            .commit();

                }

            });
            newTweetButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new NewTweetFragment())
                            .addToBackStack(null).commit();
                }

            });
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    ListView listView = (ListView) parent;
                    StatusItem item = (StatusItem) listView
                            .getItemAtPosition(position);
                    item.statusUpdate();

                }

            });
            return rootView;
        }

        private void initAdapter(){
            if (adapter != null) {
                adapter.clear();
                DBAdapter dba = new DBAdapter(rootView.getContext()
                        .getApplicationContext());
                dba.open();
                Cursor c = dba.getAllStatuses();
                if (c.moveToFirst()) {
                    do {
                        long dbId = c.getLong(0);
                        String type = c.getString(1);
                        String content = c.getString(2);
                        if (type.equals("TW")) {
                            adapter.add(new TweetItem(dbId, rootView.getContext(),
                                    content));
                        } else if (type.equals("RT")) {
                            long statusId = Long.valueOf(content);
                            adapter.add(new RetweetItem(dbId, rootView.getContext(),
                                    statusId));
                        }
                    } while (c.moveToNext());
                }
                c.close();
                dba.close();
                updateListView();
            }
        }
        public static void addItemById(long dbId) {
            if(adapter!=null){
                DBAdapter dba = new DBAdapter(rootView.getContext()
                        .getApplicationContext());
                dba.open();
                Cursor c = dba.getStatusById(dbId);
                if (c.moveToFirst()) {
                    do {
                        String type = c.getString(1);
                        String content = c.getString(2);
                        if (type.equals("TW")) {
                            adapter.add(new TweetItem(dbId, rootView.getContext(),
                                    content));
                        } else if (type.equals("RT")) {
                            long statusId = Long.valueOf(content);
                            adapter.add(new RetweetItem(dbId, rootView.getContext(),
                                    statusId));
                        }
                    } while (c.moveToNext());
                }
                c.close();
                dba.close();
            }
            updateListView();
        }

        public static void deleteItemById(Long dbId) {
            DBAdapter dba = new DBAdapter(rootView.getContext()
                    .getApplicationContext());
            dba.open();
            dba.deleteStatus(dbId);
            dba.close();
            adapter.remove(dbId);
            updateListView();
        }


        public static void updateListView() {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        private void showToast(String text) {
            Toast.makeText(rootView.getContext(), text, Toast.LENGTH_SHORT)
                    .show();
        }

    }

}
