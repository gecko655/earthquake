package jp.gecko655.earthquake;

import jp.gecko655.earthquake.db.DBAdapter;
import twitter4j.Twitter;
import android.app.Fragment;
import android.os.Bundle;
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

public class NewTweetFragment extends Fragment {

    Twitter twitter;
    View rootView;
    EditText newTweet;
    TextView tweetLength;
    final private String TW = "TW";

    public NewTweetFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_tweet, container,
                false);
        twitter = TwitterUtil.getTwitterInstance(rootView.getContext());
        Button submit = (Button) rootView.findViewById(R.id.submitNewTweet);
        tweetLength = (TextView) rootView.findViewById(R.id.newTweetLength);
        newTweet = (EditText) rootView.findViewById(R.id.newTweet);
        newTweet.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                tweetLength.setText(String.valueOf(140 - s.length()));

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String submittedText = newTweet.getText().toString();
                if (!submittedText.isEmpty()) {
                    DBAdapter dba = new DBAdapter(rootView.getContext()
                            .getApplicationContext());
                    dba.open();
                    dba.saveNote(TW, submittedText);
                    dba.close();
                    getFragmentManager().popBackStack();
                }
            }

        });

        return rootView;
    }

    private void showToast(String text) {
        Toast.makeText(rootView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
