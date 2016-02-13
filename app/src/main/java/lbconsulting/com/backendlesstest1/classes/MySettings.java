package lbconsulting.com.backendlesstest1.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Helper methods for Application Settings
 */
public class MySettings {

    public static final String NOT_AVAILABLE = "N/A";

    private static final String SETTING_ACTIVE_USER_ID = "activeUserID";
    private static final String SETTING_ACTIVE_USER_FIRST_NAME = "activeUserFirstName";
    private static final String SETTING_ACTIVE_USER_LAST_NAME = "activeUserLastName";
    private static final String SETTING_ACTIVE_USER_EMAIL = "activeUserEmail";

    private static SharedPreferences mPreferences;

    public static void setContext(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //region SETTING_ACTIVE_USER
    public static String getActiveUserID() {
        return mPreferences.getString(SETTING_ACTIVE_USER_ID, NOT_AVAILABLE);
    }

    public static void setActiveUserID(String activeUserID) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_ACTIVE_USER_ID, activeUserID);
        editor.apply();
    }

    public static String getActiveUserFirstName() {
        return mPreferences.getString(SETTING_ACTIVE_USER_FIRST_NAME, NOT_AVAILABLE);
    }

    public static void setActiveUserFirstName(String activeUserFirstName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_ACTIVE_USER_FIRST_NAME, activeUserFirstName);
        editor.apply();
    }

    public static String getActiveUserLastName() {
        return mPreferences.getString(SETTING_ACTIVE_USER_LAST_NAME, NOT_AVAILABLE);
    }

    public static void setActiveUserLastName(String activeUserName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_ACTIVE_USER_LAST_NAME, activeUserName);
        editor.apply();
    }

    public static String getActiveUserEmail() {
        return mPreferences.getString(SETTING_ACTIVE_USER_EMAIL, NOT_AVAILABLE);
    }

    public static void setActiveUserEmail(String activeUserName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_ACTIVE_USER_EMAIL, activeUserName);
        editor.apply();
    }

    public static void setActiveUserAndEmail(String userID, String firstName, String lastName, String email) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_ACTIVE_USER_ID, userID);
        editor.putString(SETTING_ACTIVE_USER_FIRST_NAME, firstName);
        editor.putString(SETTING_ACTIVE_USER_LAST_NAME, lastName);
        editor.putString(SETTING_ACTIVE_USER_EMAIL, email);
        editor.apply();
    }

    public static void resetActiveUserAndEmail() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_ACTIVE_USER_ID, NOT_AVAILABLE);
        editor.putString(SETTING_ACTIVE_USER_FIRST_NAME, NOT_AVAILABLE);
        editor.putString(SETTING_ACTIVE_USER_LAST_NAME, NOT_AVAILABLE);
        editor.putString(SETTING_ACTIVE_USER_EMAIL, NOT_AVAILABLE);
        editor.apply();
    }

    public static String getActiveUserNameAndEmail() {
        String result = "";
        String email = mPreferences.getString(SETTING_ACTIVE_USER_EMAIL, NOT_AVAILABLE);
        String firstName = mPreferences.getString(SETTING_ACTIVE_USER_FIRST_NAME, NOT_AVAILABLE);
        String lastName = mPreferences.getString(SETTING_ACTIVE_USER_LAST_NAME, NOT_AVAILABLE);

        if (!firstName.equals(MySettings.NOT_AVAILABLE) && !lastName.equals(MySettings.NOT_AVAILABLE)) {
            result = firstName + " " + lastName;
        } else if (!firstName.equals(MySettings.NOT_AVAILABLE) && lastName.equals(MySettings.NOT_AVAILABLE)) {
            result = firstName;
        } else if (firstName.equals(MySettings.NOT_AVAILABLE) && !lastName.equals(MySettings.NOT_AVAILABLE)) {
            result = lastName;
        }

        if (!email.equals(MySettings.NOT_AVAILABLE)) {
            result = result + " (" + email + ")";
        }
        return result;
    }
    //endregion

}
