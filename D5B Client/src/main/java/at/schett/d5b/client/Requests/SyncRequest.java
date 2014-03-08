package at.schett.d5b.client.Requests;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import at.schett.d5b.client.Constants.UrlConstants;
import at.schett.d5b.client.models.ProductList;

/**
 * Created by Schett on 07.03.14.
 */
public class SyncRequest extends SpringAndroidSpiceRequest<ProductList> {

    public SyncRequest() {
        super(ProductList.class);
    }

    @Override
    public ProductList loadDataFromNetwork() throws Exception {
        try {
            String url = UrlConstants.syncUrl;

            return getRestTemplate().getForObject(url, ProductList.class);
        } catch(Exception e){
            //Log.e("D5")
            throw e;
        }
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "products.list";
    }
}
