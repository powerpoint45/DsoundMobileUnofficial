package com.example.logan.github_test;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.ArrayList;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Song> songs = new ArrayList<>();
    RecyclerView songRecyclerView;
    SteemJ steemJClient;
    Button b_login;
    Button b_trending;
    Button b_hot;
    Button b_new;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initRecyclerView();
        initSteemJClient();
        initButtos();

        getSongs();


    }


    private void initButtos(){
        b_hot = findViewById(R.id.hot);
        b_login = findViewById(R.id.login);
        b_new = findViewById(R.id._new);
        b_trending = findViewById(R.id.trending);
    }
    private void initSteemJClient(){
        try {
            steemJClient = new SteemJ();
        } catch (SteemCommunicationException e) {
            e.printStackTrace();
        } catch (SteemResponseException e) {
            e.printStackTrace();
        }
    }


    private void getSongs(){
        Runnable fetch = new Runnable() {
            @Override
            public void run() {
                try {
                    songs.addAll(NetworkTools.getDsoundTrending(steemJClient));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            songRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                } catch (SteemResponseException e) {
                    e.printStackTrace();
                } catch (SteemCommunicationException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(fetch).start();
    }



    private void initRecyclerView(){
        songRecyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, songs);
        songRecyclerView.setAdapter(adapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
