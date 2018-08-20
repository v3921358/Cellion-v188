package server.api;

import java.util.ArrayList;

public class FeatureList {

    public FeatureAPI[] featureList;
    public long lastUpdated = 0;

    public class FeatureAPI {

        public int id;
        public String title, slug, summary, live_date;

    }

}
