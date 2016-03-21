package com.jktdeals.deals.Yelp;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
/**
 * Created by josevillanuva on 3/18/16.
 */
public class YelpApi2  extends DefaultApi10a {

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token arg0) {
        return null;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return null;
    }

}