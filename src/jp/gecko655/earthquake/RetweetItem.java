package jp.gecko655.earthquake;

import android.content.Context;
import twitter4j.Twitter;

public class RetweetItem extends StatusItem {

    String screenName;
    String url;
    public RetweetItem(Context context, String url) {
        this.twitter = TwitterUtil.getTwitterInstance(context);
        this.context = context;
    }

    @Override
    public String getScreenName() {
        return null;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void statusUpdate() {
        // TODO Auto-generated method stub
        
    }

}
