package at.schett.d5b.client.Constants;

/**
 * Created by Schett on 07.03.14.
 */
public class UrlConstants {

    public static final String baseUrl = "http://d5b.doebi.at%s";

    /**
     * Url Format for product sync
     * */
    public static String syncUrl = String.format(baseUrl, "/sync/");

    /**
     * Url Format d5b.doebi.at/user/barcode/
     * */
    public static String addUrl = String.format(baseUrl, "track/%s/%s/");

    public static String statsUrl = String.format(baseUrl, "/users/%s");

}
