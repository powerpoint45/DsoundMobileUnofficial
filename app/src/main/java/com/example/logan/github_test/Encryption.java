package com.example.logan.github_test;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * Class for encrypting a user's private keys
 */

class Encryption {

    private Context c;
    Encryption(Context c){
        this.c = c;
    }

    public static String CYPHER_PASSWORD = "cypher_password";
    public static String CYPHER_SC_TOKEN_P1 = "cypher_sc_token1";
    public static String CYPHER_SC_TOKEN_P2 = "cypher_sc_token2";
    public static String CYPHER_SC_REFRESH_TOKEN = "cypher_sc_refresh_token";

    /**
     * Creates a keypair for user that will hold a private key
     * @param alias keystore alias name
     * @param keyStore generated by encryptString
     */
    private void createNewKeys(String alias, KeyStore keyStore) {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(c.getApplicationContext())
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=powerpoint45, O=TeamBaconPower"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param data private key of user to encrypt
     */
    void encryptString(String data, String cypherName) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            createNewKeys("privateKeyWif",keyStore);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("privateKeyWif", null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(data.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
            sharedPref.edit().putString(cypherName, Base64.encodeToString(vals, Base64.DEFAULT)).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return decrypted private key of user
     */
    String decryptString(String cypherName) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("privateKeyWif", null);

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            SharedPreferences sharedPref =PreferenceManager.getDefaultSharedPreferences(c);
            String cipherText = sharedPref.getString(cypherName,null);
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            return new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
