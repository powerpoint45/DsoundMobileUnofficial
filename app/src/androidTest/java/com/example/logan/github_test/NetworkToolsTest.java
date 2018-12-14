package com.example.logan.github_test;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import eu.bittrade.libs.steemj.SteemJ;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NetworkToolsTest {

    //use the below commented code to access the activity for unit testing
     @Rule
     public ActivityTestRule<MainActivity> nActivityRule =
     new ActivityTestRule(MainActivity.class);

     MainActivity networkActivity;
    ArrayList<Song> songs;


    @Before
    public void setUp() throws Exception {
        networkActivity = nActivityRule.getActivity();
        networkActivity.getBaseContext();
        songs = NetworkTools.getDsoundSongs(new SteemJ(), NetworkTools.TAG_TRENDING, null, networkActivity.getBaseContext());

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getDsoundSongsSongsArrayNotNull() {
           assertNotNull(songs);
    }
    @Test
    public void getDsoundSongsSongsNotNull() {
        for(int i = 0; i < songs.size(); i++){
            assertNotNull(songs.get(i));
        }
    }
    @Test
    public void getDsoundSongsInstanceOfSong() {
        for(int i = 0; i < songs.size(); i++){
            assertTrue(songs.get(i) instanceof Song);

        }
    }
    @Test
    public void getDsoundSongsHasAuthor() {
        for(int i = 0; i < songs.size(); i++){
            assertNotNull(songs.get(i).getAuthor());
        }
    }
    @Test
    public void getDsoundSongsHasImageURL() {
        for(int i = 0; i < songs.size(); i++){
            assertNotNull(songs.get(i).getImageURL());
        }
    }
    @Test
    public void getDsoundSongsHasSongURL() {
        for(int i = 0; i < songs.size(); i++){
            assertNotNull(songs.get(i).getSongURL());
        }
    }
    @Test
    public void getDsoundSongsHasSongTitle() {
        for(int i = 0; i < songs.size(); i++){
            assertNotNull(songs.get(i).getTitle());
        }
    }
}

