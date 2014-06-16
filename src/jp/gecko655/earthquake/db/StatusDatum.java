package jp.gecko655.earthquake.db;

import java.io.Serializable;

public class StatusDatum implements Serializable {

    public static final String TABLE_NAME = "tweet_and_retweet";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_KIND = "kind";
    public static final String COLUMN_CONTENT = "content";
    public StatusDatum() {
        // TODO Auto-generated constructor stub
    }

}
