package shoppinglist.android.schumann.max.shoppinglist.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by max on 12.05.16.
 *
 * Product model
 */
public class Product implements Parcelable {

    private long   id       = -1L;
    private String name;
    private Double quantity = 1.0;
    //TODO: may handle the unit as an enum on the client side
    private String unit;
    private String shop;
    private Boolean isSelected = false;

    public Product (String name, Double quantity, String unit, String listName) {
        this.name     = name;
        this.quantity = quantity == null ? 1.0 : quantity;
        this.unit     = unit;
        this.shop = listName;
        isSelected = false;
    }

    private Product(Parcel parcel) {
        id       = parcel.readLong();
        name     = parcel.readString();
        quantity = parcel.readDouble();
        unit     = parcel.readString();
        shop = parcel.readString();
        isSelected = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeDouble(quantity);
        parcel.writeString(unit);
        parcel.writeString(shop);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>(){

        @Override
        public Product createFromParcel(Parcel parcel) {
            return new Product(parcel);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getShop() {
        return shop;
    }

    public long getId() {
        return id;
    }

    public Boolean isSelected() {
        if (isSelected == null)
            isSelected = false;
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
