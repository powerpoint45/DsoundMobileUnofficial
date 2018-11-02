package com.example.logan.github_test;

import android.util.Log;

import com.google.api.client.json.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.base.models.DiscussionQuery;
import eu.bittrade.libs.steemj.enums.DiscussionSortType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class NetworkTools {

    public static String IPFS_URL = "https://gateway.ipfs.io/ipfs/";

    public static ArrayList<Song> getDsoundSongs(SteemJ steemJ, String tag) throws SteemResponseException, SteemCommunicationException {

        ArrayList<Song> songs = new ArrayList<>();

        DiscussionQuery discussionQuery = new DiscussionQuery();
        discussionQuery.setTag("dsound");
        discussionQuery.setLimit(50);
        List<Discussion> discussions;
        switch (tag){
            case "Trending":
                 discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_TRENDING);
            break;
            case "Hot":
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_HOT);
            break;
            case "New":
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_CREATED);
            break;

            default:
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_FEED);
        }



        boolean failedAdding;
        for (Discussion d: discussions) {
            failedAdding = false;
            Song song = new Song();
            song.setAuthor(d.getAuthor().getName());
            song.setDate(d.getFirstRebloggedOn());
            song.setPermlink(d.getPermlink().getLink());
            song.setTitle(d.getTitle());
            Log.d("SC",d.getJsonMetadata());

            try {

                JSONObject jObj = new JSONObject(d.getJsonMetadata());

                song.setImageURL(IPFS_URL+jObj.getJSONObject("audio").getJSONObject("files").getString("cover"));
                song.setSongURL(IPFS_URL+jObj.getJSONObject("audio").getJSONObject("files").getString("sound"));
                song.setDuration((int)jObj.getJSONObject("audio").getDouble("duration"));
            } catch (JSONException e) {
                e.printStackTrace();
                failedAdding = true;
            }

            if (!failedAdding)
                songs.add(song);
        }

        return songs;

    }

}
