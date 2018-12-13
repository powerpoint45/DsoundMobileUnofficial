package com.example.logan.github_test;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hapramp.steemconnect4j.SteemConnectCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.base.models.DiscussionQuery;
import eu.bittrade.libs.steemj.base.models.VoteState;
import eu.bittrade.libs.steemj.enums.DiscussionSortType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

class NetworkTools extends AppCompatActivity {

    private static String IPFS_URL = //"https://cloudflare-ipfs.com/ipfs/";
            "https://gateway.ipfs.io/ipfs/";

    //My personal server built to deal with refresh tokens with steemConnect for this app
    private static String SC_AUTH_URL = "https://dsoundmobile.herokuapp.com/auth";

    // each integer variable represents the user's song list sort preference:
    static final int TAG_TRENDING = 0;
    static final int TAG_HOT = 1;
    static final int TAG_NEW = 2;
    static final int TAG_FEED = 3;

    //number of songs to retrieve at a time
    private static final int SONG_RETRIEVE_LIMIT = 20;

    /**
     * method gets a list of songs from server with specific tag.
     * method must be run in a non UI thread
     * @param steemJ client
     * @param tag TAG_TRENDING, TAG_HOT, TAG_NEW, or TAG_FEED
     * @param lastSong last songLoading in list so that more songs can be loaded than the limit.
     *                     param can be null for fetching the first items
     * @param c android context
     * @return ArrayList of songs
     * @throws SteemResponseException problem with Steem client has occurred
     * @throws SteemCommunicationException problem with Steem client has occurred
     */
    static ArrayList<Song> getDsoundSongs(SteemJ steemJ, int tag, Song lastSong, Context c) throws SteemResponseException, SteemCommunicationException {

        ArrayList<Song> songs = new ArrayList<>();

        DiscussionQuery discussionQuery = new DiscussionQuery();
        discussionQuery.setTag("dsound");

        if (lastSong!=null) {
            Log.d("ds","setStartPermlink "+lastSong.getTitle());
                discussionQuery.setStartPermlink(lastSong.getPermlink());
                discussionQuery.setStartAuthor(lastSong.getAuthor());
        }


        discussionQuery.setLimit(SONG_RETRIEVE_LIMIT);

        List<Discussion> discussions;

        // sort the discussions list by user preference:
        switch (tag){
            case TAG_TRENDING:
                 discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_TRENDING);
                break;
            case TAG_HOT:
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_HOT);
                break;
            case TAG_NEW:
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_CREATED);
                break;
                // case TAG_FEED; this list is the default sorting list when the user first opens the app
            default:
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_FEED);
                break;
        }



        boolean failedAdding;
        for (Discussion d: discussions) {
            //prevent adding the same songLoading as the last songLoading in list
            if (lastSong == null || (!d.getPermlink().getLink().equals(lastSong.getPermlink().getLink()))) {
                failedAdding = false;
                Song song = new Song();
                song.setAuthor(d.getAuthor());
                song.setDate(d.getFirstRebloggedOn());
                song.setPermlink(d.getPermlink());
                song.setTitle(d.getTitle());
                song.setTag(tag);

                String appAccountName = Tools.getAccountName(c);

                if (Tools.getAccountName(c)!=null) {
                    for (VoteState v : d.getActiveVotes()) {
                        if (v.getVoter().getName().equals(appAccountName)) {
                            if (v.getPercent()>0)
                                song.setAccountFavoritedSong(true);
                            break;
                        }
                    }
                }

                Log.d("dsjson", d.getJsonMetadata());
                JSONObject jObj = null;
                if (d.getJsonMetadata()!=null) {
                    try {
                        jObj = new JSONObject(d.getJsonMetadata());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (jObj!=null) {
                    //Get all absolute required tags for song
                    //if any fail then don't add to list of songs
                    try {
                        if (jObj.has("audio")) {
                            song.setImageURL(IPFS_URL + jObj.getJSONObject("audio").getJSONObject("files").getString("cover"));
                            song.setSongURL(IPFS_URL + jObj.getJSONObject("audio").getJSONObject("files").getString("sound"));
                            song.setDuration((int) jObj.getJSONObject("audio").getDouble("duration"));
                        }else
                            failedAdding = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        failedAdding = true;
                    }

                    //get direct link to audio if provided by dsound. May load songs faster that standard IPFS gateway
                    try {
                        if (jObj.has("links")) {
                            JSONArray arr = jObj.getJSONArray("links");
                            for (int i = 0; i < arr.length(); i++) {
                                if (arr.getString(i).contains("/ipfs/")) {
                                    Log.d("ds", arr.getString(i));
                                    song.setDsoundSongURL(arr.getString(i));
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!failedAdding)
                    songs.add(song);
            }
        }
        return songs;
    }

    static SteemConnectResult getAccessTokenFromCode(String code){
        SteemConnectResult result = new SteemConnectResult();

        JSONObject jsonObject = null;

        try {
            jsonObject = getServerData(SC_AUTH_URL+"?code="+code);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject!=null) {
            Log.d("ds", "JOBG:" + jsonObject.toString());

            try {
                if (jsonObject.has("access_token")) {
                    result.setAccessToken(jsonObject.getString("access_token"));
                }

                if (jsonObject.has("refresh_token")) {
                    result.setRefreshToken(jsonObject.getString("refresh_token"));
                }

                if (jsonObject.has("username")) {
                    result.setUserName(jsonObject.getString("username"));
                    Log.d("ds",result.getUserName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    static SteemConnectResult getAccessTokenFromRefreshToken(String refreshToken){
        SteemConnectResult result = new SteemConnectResult();

        JSONObject jsonObject = null;

        try {
            jsonObject = getServerData(SC_AUTH_URL+"?refresh_token="+refreshToken);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject!=null) {
            Log.d("ds", "JOBG:" + jsonObject.toString());

            try {
                if (jsonObject.has("access_token")) {
                    result.setAccessToken(jsonObject.getString("access_token"));
                }

                if (jsonObject.has("refresh_token")) {
                    result.setRefreshToken(jsonObject.getString("refresh_token"));
                }

                if (jsonObject.has("username")) {
                    result.setUserName(jsonObject.getString("username"));
                    Log.d("ds",result.getUserName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    static JSONObject getServerData(String urlWeb) throws IOException, JSONException {
        // Connect to the URL using java's native library
        URL url = new URL(urlWeb);
        URLConnection request = url.openConnection();
        request.connect();

        BufferedReader rd = new BufferedReader(new InputStreamReader((InputStream) request.getContent()
                , Charset.forName("UTF-8")));

        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return new JSONObject(sb.toString());
    }


}
