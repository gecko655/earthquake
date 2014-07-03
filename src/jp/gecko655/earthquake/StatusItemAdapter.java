package jp.gecko655.earthquake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatusItemAdapter extends ArrayAdapter<StatusItem> {

    private int textViewResourceId;
    private Map<Long,StatusItem> items;
    private LayoutInflater inflater;

    public StatusItemAdapter(Context context, int resource) {
        this(context, resource, new LinkedHashMap<Long,StatusItem>());
    }

    public StatusItemAdapter(Context context, int resource,
            LinkedHashMap<Long,StatusItem> items) {
        super(context, resource);
        this.textViewResourceId = resource;
        this.items = items;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void add(StatusItem item) {
        super.add(item);
        items.put(item.getDBId(),item);
    }

    @Override
    public void remove(StatusItem item) {
        super.remove(item);
        items.remove(item.getDBId());
    }
    public void remove(long dbId) {
        remove(items.get(dbId));
    }
    
    public boolean containsId(long dbId){
        return items.keySet().contains(dbId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final StatusItem item;
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(textViewResourceId, null);
        }

        if (items.size() > position) {
            //Get the status item at this position.
            item = (new ArrayList<StatusItem>(items.values())).get(position);

            ((TextView) view.findViewById(R.id.ScreenName)).setText(item
                    .getScreenName());
            ((TextView) view.findViewById(R.id.TweetContent)).setText(item
                    .getContent());
            ((TextView) view.findViewById(R.id.deleteButton)).setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {
                    MainActivity.PlaceholderFragment.deleteItemById(item.getDBId());
                }
                
            });
        }

        return view;
    }


}
