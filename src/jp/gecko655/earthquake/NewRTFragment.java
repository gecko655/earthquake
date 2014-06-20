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

public class NewRTFragment extends Fragment{

    Twitter twitter;
    View rootView;
    EditText newRTId;
    final private String RT="RT";

    public NewRTFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_tweet, container,
                false);
        twitter = TwitterUtil.getTwitterInstance(rootView.getContext());
        Button submit = (Button)rootView.findViewById(R.id.submitNewTweet);
        newRTId = (EditText)rootView.findViewById(R.id.newTweet);
        newRTId.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
            
        });
            
        submit.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	try{
                    String idString =newRTId.getText().toString();
                    long id = Long.valueOf(idString);
                    if(new RetweetItem(rootView.getContext(),id).isValid()){
                        DBAdapter dba = new DBAdapter(rootView.getContext().getApplicationContext());
                        dba.open();
                        dba.saveNote(RT, idString);
                        dba.close();
                        getFragmentManager().popBackStack();
                    }else{
                    	showToast("Invalid tweet id");
                    	
                    }
            	}catch(NumberFormatException e){
            		
            	}
            }
            
        });
        

        return rootView;
    }

    private void showToast(String text) {
        Toast.makeText(rootView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
