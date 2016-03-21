package com.jktdeals.deals.Yelp;

import android.content.Context;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


/**
 * Created by josevillanuva on 3/18/16.
 */
public class Yelp {

    private static final String CONSUMER_KEY = "CJFPfd136_OAHXqMkOKk3Q";
    private static final String CONSUMER_SECRET = "_YVVlE6NnaDobNyUBJbOCMrLvvE";
    private static final String TOKEN = "Ba9ZzKzqvV77k_Y5vSlM_KEf34rWoZcL";
    private static final String TOKEN_SECRET = "49CDASfkwaxD4XQAvTExBguuyb4";

    OAuthService service;
    Token accessToken;

    public static Yelp getYelp(Context context) {
        return new Yelp(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
    }

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
     *
     * @param consumerKey Consumer key
     * @param consumerSecret Consumer secret
     * @param token Token
     * @param tokenSecret Token secret
     */
    public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    /**
     * Search with term and location.
     *
     * @param term Search term
     * @param latitude Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public String search(String term, double latitude, double longitude) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public String search(String term, String location, String resultLimit) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", resultLimit);
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    // CLI
    public static void main(String[] args) {
        // Update tokens here from Yelp developers site, Manage API access.
        String consumerKey = "";
        String consumerSecret = "";
        String token = "";
        String tokenSecret = "";

        Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
        String response = yelp.search("burritos", 30.361471, -87.164326);

        System.out.println(response);
    }
}
