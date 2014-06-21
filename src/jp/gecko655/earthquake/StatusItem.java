package jp.gecko655.earthquake;

import android.content.Context;
import twitter4j.Twitter;

public abstract class StatusItem {
    String content;
    Twitter twitter;
    Context context;
    long dbId;

    public abstract String getScreenName();

    public abstract String getContent();

    public abstract void statusUpdate();

}
