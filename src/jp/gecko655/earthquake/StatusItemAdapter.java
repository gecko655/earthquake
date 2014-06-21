package jp.gecko655.earthquake;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Long> dbIds;

    public StatusItemAdapter(Context context, int resource) {
        this(context, resource, new ArrayList<StatusItem>());
    }

    public StatusItemAdapter(Context context, int resource,
            List<StatusItem> items) {
        super(context, resource);
        this.context = context;
        this.textViewResourceId = resource;
        this.items = items;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dbIds = new HashSet<Long>();
        for(StatusItem item: items){
            dbIds.add(item.getDBId());
        }
    }

    @Override
    public void add(StatusItem item) {
        super.add(item);
        items.add(item);
        dbIds.add(item.getDBId());
    }

    @Override
    public void remove(StatusItem item) {
        super.remove(item);
        items.remove(item);
        dbIds.remove(item.getDBId());
    }
    
    public boolean containsId(long dbId){
        return dbIds.contains(dbId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(textViewResourceId, null);
        }

        if (items.size() > position) {
            StatusItem item = items.get(position);

            ((TextView) view.findViewById(R.id.ScreenName)).setText(item
                    .getScreenName());
            ((TextView) view.findViewById(R.id.TweetContent)).setText(item
                    .getContent());
        }

        return view;
    }

}
