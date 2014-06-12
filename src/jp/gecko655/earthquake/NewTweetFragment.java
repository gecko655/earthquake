package jp.gecko655.earthquake;

import twitter4j.Twitter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class NewTweetFragment extends Fragment{

    Twitter twitter;
    View rootView;

    public NewTweetFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_tweet, container,
                false);
        twitter = TwitterUtil.getTwitterInstance(rootView.getContext());
        Button submit = (Button)rootView.findViewById(R.id.submitNewTweet);
        submit.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
            
        });
        

        return rootView;
    }

    private void showToast(String text) {
        Toast.makeText(rootView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
