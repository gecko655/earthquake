package jp.gecko655.earthquake;

import jp.gecko655.earthquake.db.DBAdapter;
import twitter4j.Status;
import twitter4j.Twitter;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewRTFragment extends Fragment implements LoaderCallbacks<Status> {

    Twitter twitter;
    View rootView;
    EditText newRTId;
    final private String RT = "RT";
    private final int LOADER_ID = 0;

    public NewRTFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_retweet, container,
                false);
        twitter = TwitterUtil.getTwitterInstance(rootView.getContext());
        Button submit = (Button) rootView.findViewById(R.id.submitNewRetweet);
        newRTId = (EditText) rootView.findViewById(R.id.newRetweet);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showToast("Loading...");
                    String idString = newRTId.getText().toString();
                    long id = Long.valueOf(idString);
                    Bundle args = new Bundle();
                    args.putLong("statusId", id);
                    getLoaderManager().initLoader(LOADER_ID, args,
                            NewRTFragment.this).forceLoad();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Status> onCreateLoader(int id, Bundle args) {
        long statusId = args.getLong("statusId");
        return new StatusLoader(rootView.getContext(), statusId);
    }

    @Override
    public void onLoadFinished(Loader<Status> loader, Status data) {
        if (data == null) {
            showToast("Invalid tweet id");
        } else {
            String idString = String.valueOf(data.getId());
            DBAdapter dba = new DBAdapter(rootView.getContext()
                    .getApplicationContext());
            dba.open();
            long dbId = dba.saveNote(RT, idString);
            dba.close();
            MainActivity.PlaceholderFragment.addItemById(dbId);
            showToast("New RT was created:\n"
                    + "@"+ data.getUser().getScreenName()+ "\n"
                    + "\t"+data.getText()+"\n");
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    NewRTFragment.this.getFragmentManager().popBackStack();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Status> loader) {
        // TODO Auto-generated method stub

    }

    private void showToast(String text) {
        Toast.makeText(rootView.getContext(), text, Toast.LENGTH_LONG).show();
    }

}
