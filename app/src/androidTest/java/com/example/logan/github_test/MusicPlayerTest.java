package com.example.logan.github_test;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import eu.bittrade.libs.steemj.SteemJ;

import static org.junit.Assert.assertNotNull;

public class MusicPlayerTest {

    @Rule
    public ActivityTestRule<MainActivity> nActivityRule =
            new ActivityTestRule(MainActivity.class);

    private MainActivity mainActivity;
    private ArrayList<Song> songs;

    @Before
    public void setUp() throws Exception {
        mainActivity = nActivityRule.getActivity();
        songs = NetworkTools.getDsoundSongs(new SteemJ(), NetworkTools.TAG_TRENDING, null, mainActivity.getBaseContext());

        Song blankSong = new Song();
        songs.add(blankSong);

        Song invalidSong = new Song();
        invalidSong.setSongURL("https://gateway.ipfs.io/ipfs/INVALIDURL");
        songs.add(invalidSong);

        Song badFormattedSong = new Song();
        invalidSong.setSongURL("lkzsfnhcriawkcniaze");
        songs.add(badFormattedSong);
    }

    @After
    public void tearDown() throws Exception {
    }


    /**
     * A test to make sure no error occurs when any of the loaded songs are called to play
     */
    @Test
    public void playSongTest() {
        for(int i = 0; i < songs.size(); i++){
            MusicPlayer.getInstance().play(songs.get(i),mainActivity);
            try {
                //Give time for song to load
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests getCurrentSongPlaying method with all loaded songs
     * This test will take a long time
     */
    @Test
    public void getCurrentSongPlayingTest() {
        for(int i = 0; i < songs.size(); i++){
            MusicPlayer.getInstance().play(songs.get(i),mainActivity);
            try {
                //Give time for song to load
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (MusicPlayer.getInstance().isPlaying()) {
                Log.d("ds","Testing getCurrentSongPlaying");
                assertNotNull(MusicPlayer.getInstance().getCurrentSongPlaying());
            }
        }
    }

    /**
     * A test to ensure getTimeString works on different loaded songs
     * This test will take a long time
     */
    @Test
    public void getTimeStringTest() {
        for(int i = 0; i < songs.size(); i++){
            MusicPlayer.getInstance().play(songs.get(i),mainActivity);
            try {
                //Give time for song to load
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (MusicPlayer.getInstance().isPlaying()) {
                Log.d("ds","Testing getTimeString");
                assertNotNull(MusicPlayer.getInstance().getTimeString());
            }
        }
    }


}