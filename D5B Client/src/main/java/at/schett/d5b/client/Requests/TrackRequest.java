package at.schett.d5b.client.Requests;

import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpMethod;

import at.schett.d5b.client.Constants.UrlConstants;
import at.schett.d5b.client.models.Product;

/**
 * Created by Schett on 08.03.14.
 */
public class TrackRequest extends SpringAndroidSpiceRequest<Product> {

    private Product product;

    private String userName;

    public TrackRequest(Product product, String user) {
        super(Product.class);
        this.product = product;
        userName = user;
    }

    @Override
    public Product loadDataFromNetwork() throws Exception {
        try {
            String url = String.format(UrlConstants.addUrl, userName, product.getBarcode());

            getRestTemplate().execute(url, HttpMethod.GET, null, null);
            return product;
        } catch (Exception e) {
            Log.e("D5BClient", "Exception during network loading...", e);
            throw e;
        }
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return "products.list";
    }
}
