package at.schett.d5b.client.Requests;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;

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
        Cursor c = parentActivity.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        int count = c.getCount();
        String userName = "";
        String[] columnNames = c.getColumnNames();
        boolean b = c.moveToFirst();
        int position = c.getPosition();
        if (count == 1 && position == 0) {
            for (int j = 0; j < columnNames.length; j++) {
                if (columnNames[j].equals("display_name")) {
                    String columnValue = c.getString(c.getColumnIndex(columnNames[j]));
                    userName = columnValue;
                }
            }
            c.close();
        }
        return userName;
    }
}
