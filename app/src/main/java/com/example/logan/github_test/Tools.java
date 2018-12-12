package com.example.logan.github_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import afu.org.checkerframework.checker.nullness.qual.NonNull;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;

/**
 * General class for certain methods used throughout app.
 */
class Tools {
    /**
     * @param c Android context
     * @param cypher CYPHER_PASSWORD or CYPHER_SC_TOKEN
     * @return private key that was saved earlier
     */
    static String getUserPrivateKey(Context c, String cypher){
        Encryption encryption = new Encryption(c);
        return encryption.decryptString(cypher);
    }

    /**
     * @param c Android context
     * @return account name of user logged into app
     */
    static String getAccountName(Context c){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPref.getString("username",null);
    }

    /**
     * @param username to save
     * @param privateKey to save
     * @param c Android context
     * @param cypher CYPHER_PASSWORD or CYPHER_SC_TOKEN
     */
    static void saveUserCredentials(String username, String privateKey, Context c, String cypher){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        sharedPref.edit().putString("username", username).apply();

        Encryption encryption = new Encryption(c);
        encryption.encryptString(privateKey, cypher);
    }

    /**
     * @param userName to log in with
     * @param password to log in with
     * @param c Android context
     * @return Account object
     * @throws InvalidKeyException if login fails
     */
    static Account loginIfPossible(String userName, String password, Context c) throws InvalidKeyException {
        SteemJConfig myConfig = SteemJConfig.getInstance();
        myConfig.setResponseTimeout(100000);
        myConfig.setDefaultAccount(new AccountName(userName));
        List<ImmutablePair<PrivateKeyType, String>> privateKeys = new ArrayList<>();
        privateKeys.add(new ImmutablePair<>(PrivateKeyType.POSTING, password));

        try {
            myConfig.getPrivateKeyStorage().addAccount(myConfig.getDefaultAccount(), privateKeys);
            Account a = new Account();
            a.setUserName(getAccountName(c));
            return a;
        }catch (Exception e){
            throw new InvalidKeyException();
        }
    }

    /**
     * @param c Android context
     * @return Account object if username & password were saved earlier
     * @throws InvalidKeyException if login fails due to private key
     */
    static Account loginIfPossible(Context c) throws InvalidKeyException {
        if (getAccountName(c)!=null && getUserPrivateKey(c, Encryption.CYPHER_PASSWORD)!=null){
            return loginIfPossible(getAccountName(c),getUserPrivateKey(c, Encryption.CYPHER_PASSWORD),c);
        }
        return null;
    }

    /**
     * removes saved username from preferences
     * @param c Android context
     */
    static void forgetLogin(Context c){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        sharedPref.edit().remove("username")
                .remove(Encryption.CYPHER_PASSWORD)
                .remove(Encryption.CYPHER_SC_TOKEN).apply();
    }


    /**
     * @param context android context
     * @return true if Chrome Custom Tabs are available
     */
    static boolean isChromeCustomTabsSupported(@NonNull final Context context) {
        final String SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
        final String CHROME_PACKAGE = "com.android.chrome";

        Intent serviceIntent = new Intent(SERVICE_ACTION);
        serviceIntent.setPackage(CHROME_PACKAGE);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !(resolveInfos == null || resolveInfos.isEmpty());
    }
}
