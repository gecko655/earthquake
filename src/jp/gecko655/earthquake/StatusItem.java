package jp.gecko655.earthquake;

import twitter4j.Twitter;
import android.content.Context;

public abstract class StatusItem {
    String content;
    Twitter twitter;
    Context context;
    long dbId;

    public abstract String getScreenName();

    public abstract String getContent();

    public abstract void statusUpdate();

    public Long getDBId() {
        return dbId;
    }

}
