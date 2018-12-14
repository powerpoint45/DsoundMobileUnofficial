package com.example.logan.github_test;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

public class LoginActivityTest {

    //use the below commented code to access the activity for unit testing
    @Rule
    public ActivityTestRule<LoginActivity> nActivityRule =
            new ActivityTestRule(LoginActivity.class);

    LoginActivity loginActivity;
    ArrayList<Song> songs;

    @Before
    public void setUp() throws Exception {
      loginActivity = nActivityRule.getActivity();
      // loginActivity.getBaseContext();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkNullUserName() {
        assertNotNull(loginActivity.usernameEditText);
    }

    @Test
    public void checkNullPassword() {
        assertNotNull(loginActivity.passwordEditText);
    }

}
