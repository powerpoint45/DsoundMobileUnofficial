package com.example.logan.github_test;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    MainActivity mainActivity;

    @Before
    public void setUp() throws Exception{
        mainActivity = mActivityRule.getActivity();
        mainActivity.initMusicBar();

    }

    @After
    public void tearDown() throws Exception{
    }
    @Test
    public void initMusicBar() {
        assertNotNull(mainActivity.trendingButton);

    }

    @Test
    public void updateMusicBar() {

    }
}