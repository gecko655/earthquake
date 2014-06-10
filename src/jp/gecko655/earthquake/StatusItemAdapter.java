package jp.gecko655.earthquake;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatusItemAdapter extends ArrayAdapter<StatusItem> {

    private Context context;
    private int textViewResourceId;
    private List<StatusItem> items;
    private LayoutInflater inflater;
    public StatusItemAdapter(Context context, int resource) {
        this(context,resource,new ArrayList<StatusItem>());
    }
    public StatusItemAdapter(Context context, int resource,List<StatusItem> items) {
        super(context, resource);
        this.context=context;
        this.textViewResourceId = resource;
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public void add(StatusItem item){
        super.add(item);
        items.add(item);
    }

    @Override
    public void remove(StatusItem item){
        super.remove(item);
        items.remove(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(textViewResourceId, null);
        }

        if(items.size()>position){
            StatusItem item = items.get(position);

            ((TextView) view.findViewById(R.id.ScreenName)).setText(item.getScreenName());
            ((TextView) view.findViewById(R.id.TweetContent)).setText(item.getContent());
        }

        return view;
    }

}
