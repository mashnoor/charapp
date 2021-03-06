package com.origo.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Menon on 7/24/16.
 */
public class HelperClass {


    //Constants
    final public static String ADD_USER_URL = "http://192.168.0.160/adduser";

    final public static String ADD_FRIEND_URL = "http://192.168.0.160/addfriends/";

    final public static String UPLOAD_FILE = "http://192.168.0.160/addpost/";
    final public static String FRIENDS_LATEST_POSTS = "http://192.168.0.160/latestposts/";

    final public static String GET_AUDIO_FILE = "http://192.168.0.160/getfile/";

    final public static String GET_IMAGE_FILE = "http://192.168.0.160/getimage/";

    final public static String UPLOAD_IMAGE= "http://192.168.0.160/addimage/";

    final public static String GET_FRIENDS = "http://192.168.0.160/getfriends/";


    public void saveName(Context context, String name)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("origo", Context.MODE_PRIVATE).edit();
        editor.putString("username", name);

        editor.apply();
    }


    public static void savePhone(Context context, String phone)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences("origo", Context.MODE_PRIVATE).edit();
        editor.putString("userphone", phone);

        editor.apply();
    }

    public String getName(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("origo", Context.MODE_PRIVATE);


            String name = prefs.getString("name", "NO");//"No name defined" is the default value.
           return name;

    }


    public static String getPhone(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("origo", Context.MODE_PRIVATE);


        String phone = prefs.getString("userphone", "NO");//"No name defined" is the default value.
        return phone;

    }

}
