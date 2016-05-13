package shoppinglist.android.schumann.max.shoppinglist.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import shoppinglist.android.schumann.max.shoppinglist.R;
import shoppinglist.android.schumann.max.shoppinglist.models.Product;

/**
 * Created by max on 13.05.16.
 */
public class ShoppingListAdapter extends BaseAdapter{

    private       ArrayList<Product> listItems;
    private final Context            context;

    public ShoppingListAdapter(Context context, ArrayList<Product> items) {
        this.context = context;
        listItems = items;
    }

    public ShoppingListAdapter(Context context) {
        this.context = context;
        listItems = new ArrayList<>();
    }

    public void setListItems(ArrayList<Product> items) {
        this.listItems = items;
    }

    public void add(Product product) {
        listItems.add(product);
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int index) {
        return listItems.get(index);
    }

    @Override
    public long getItemId(int index) {
        return listItems.get(index).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View     rowRootView     = inflater.inflate(R.layout.shopping_list_item, viewGroup, false);
        TextView productName     = (TextView) rowRootView.findViewById(R.id.product_name_textview);
        TextView productQuantity = (TextView) rowRootView.findViewById(R.id.product_quantity_textview);
        TextView productUnit     = (TextView) rowRootView.findViewById(R.id.product_unit_textview);
        CheckBox productCheckBox = (CheckBox) rowRootView.findViewById(R.id.product_shoppingcart_checkbox);

        Product product = listItems.get(position);

        productName.setText(product.getName());
        productQuantity.setText(product.getQuantity().toString());
        productUnit.setText(product.getUnit());
        productCheckBox.setChecked(product.isSelected());

        return rowRootView;
    }
}
