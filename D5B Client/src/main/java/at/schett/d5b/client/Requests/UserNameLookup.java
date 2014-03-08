package at.schett.d5b.client.Requests;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by Schett on 08.03.14.
 */
public class UserNameLookup extends SpringAndroidSpiceRequest<String> {

    Activity parentActivity;

    public UserNameLookup(Activity activity) {
        super(String.class);
        parentActivity = activity;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        try {
            String userName;
            Account[] acc = AccountManager.get(parentActivity.getApplicationContext()).getAccountsByType("com.google");
            if (acc != null && acc[0] != null) {
                userName = acc[0].name.split("@")[0];
            } else {
                userName = "";
            }
            return userName;
        } catch(NullPointerException e) {
            Log.d("d5b.client", "Null pointer when trying to get the accounts", e);
            throw e;
        }
    }
}
