package at.schett.d5b.client.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import at.schett.d5b.client.Constants.AppConstants;
import at.schett.d5b.client.Constants.UrlConstants;
import at.schett.d5b.client.R;
import at.schett.d5b.client.Requests.SyncRequest;
import at.schett.d5b.client.Requests.TrackRequest;
import at.schett.d5b.client.Requests.UserNameLookup;
import at.schett.d5b.client.enums.RequestCode;
import at.schett.d5b.client.models.Product;
import at.schett.d5b.client.models.ProductList;


public class MainActivity extends Activity {

    protected SpiceManager spiceManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
    private String lastRequestCacheKey;

    private ProductList productList;
    private Intent parentIntent;

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    private void performSyncRequest() {
        MainActivity.this.setProgressBarIndeterminateVisibility(true);

        SyncRequest request = new SyncRequest();
        lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new ProductSyncRequestListener());
    }

    private void performTrackRequest(Product product) {
        MainActivity.this.setProgressBarIndeterminateVisibility(true);

        TrackRequest request = new TrackRequest(product, AppConstants.UserName);
        lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new TrackRequestListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
        performSyncRequest();
        performUserNameLookup();

    }

    public void performUserNameLookup() {
        UserNameLookup request = new UserNameLookup(this);
        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new UserNameLookupListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_show_stats:
                Intent intent = new Intent(this, WebViewActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sync:
                performSyncRequest();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(View view) {
        if (productList == null) {
            showToast("Product list is not loaded please sync with server");
        } else {
            if (view.getId() == R.id.scan_button) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, RequestCode.ScanRequestCode);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case RequestCode.ScanRequestCode:
                if (resultCode == RESULT_OK) {
                    parentIntent = intent;
                    String barCode = intent.getStringExtra("SCAN_RESULT");
                    Product productToAdd = null;
                    for(Product product : productList) {
                        if (product.getBarcode().equals(barCode)) {
                            productToAdd = product;
                        }
                    }

                    if (productToAdd != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        String productName = productToAdd.getName();
                        builder.setMessage(String.format("Do you want to add %s to your drinks?", productName)).setTitle(R.string.dialog_title);
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes, of course", new OkOnClickListener(productToAdd));
                        builder.setNegativeButton("No, definitely not", new CancelOnClickListener());

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        showToast("Product not found, please try to sync products");
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    showToast("No scan data received");
                    Log.i("xZing", "Cancelled");
                }
                break;
        }
    }


    protected void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private class ProductSyncRequestListener implements RequestListener<ProductList> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            MainActivity.this.setProgressBarIndeterminateVisibility(false);
            productList = null;
            showToast(String.format("Error during sync..."));
            Log.d("RequestFailure", "Error during sync", spiceException);
        }

        @Override
        public void onRequestSuccess(ProductList products) {
            MainActivity.this.setProgressBarIndeterminateVisibility(false);
            productList = products;
            showToast(String.format("Synced %d products...", productList.size()));
        }
    }

    private class TrackRequestListener implements RequestListener<Product> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            MainActivity.this.setProgressBarIndeterminateVisibility(false);
            productList = null;
            showToast(String.format("Error during sync..."));
            Log.d("RequestFailure", "Error during sync", spiceException);
        }

        @Override
        public void onRequestSuccess(Product product) {
            MainActivity.this.setProgressBarIndeterminateVisibility(false);
            showToast(String.format("Added %s to your consumed drinks", product.getName()));
        }
    }

    private class UserNameLookupListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            AppConstants.UserName = "";
            showToast("No user name found...");
        }

        @Override
        public void onRequestSuccess(String s) {
            AppConstants.UserName = s.replace(' ', '_');
            //showToast(String.format("Found username %s", userName));
        }
    }


    private class CancelOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // DO NOTHING
        }
    }

    private class OkOnClickListener implements DialogInterface.OnClickListener {

        private Product product;

        OkOnClickListener(Product product) {
            this.product = product;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String contents = parentIntent.getStringExtra("SCAN_RESULT");
            Log.i("xZing", "contents: " + contents);
            // Handle successful scan
            performTrackRequest(product);
        }
    }
}
