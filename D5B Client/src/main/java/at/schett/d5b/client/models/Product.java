package at.schett.d5b.client.models;

/**
 * Created by Schett on 07.03.14.
 */
public class Product {

    private String name;

    private String barcode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String toString() {
        return name;
    }
}
