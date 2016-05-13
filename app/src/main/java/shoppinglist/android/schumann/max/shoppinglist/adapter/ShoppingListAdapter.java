package shoppinglist.android.schumann.max.shoppinglist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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
        super.notifyDataSetChanged();
    }

    public void add(Product product) {
        listItems.add(product);
        super.notifyDataSetChanged();
    }

    public void remove(Product product) {
        listItems.remove(product);
        super.notifyDataSetChanged();
    }

    public ArrayList<Product> getSelectedItems() {
        ArrayList<Product> selected = new ArrayList<>();
        for (Product product: listItems){
            if (product.isSelected()){
                selected.add(product);
            }
        }
        return selected;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Product getItem(int index) {
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

        final Product product = listItems.get(position);

        final View     rowRootView     = inflater.inflate(R.layout.shopping_list_item, viewGroup, false);
        TextView productName     = (TextView) rowRootView.findViewById(R.id.product_name_textview);
        TextView productQuantity = (TextView) rowRootView.findViewById(R.id.product_quantity_textview);
        TextView productDash     = (TextView) rowRootView.findViewById(R.id.product_dash_textview);
        TextView productUnit     = (TextView) rowRootView.findViewById(R.id.product_unit_textview);
        CheckBox productCheckBox = (CheckBox) rowRootView.findViewById(R.id.product_shoppingcart_checkbox);

        productCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                product.setSelected(b);
                sort();
            }
        });

        productName.setText(product.getName());
        productQuantity.setText(product.getQuantity().toString());
        productUnit.setText(product.getUnit());
        productCheckBox.setChecked(product.isSelected());
        if (product.getUnit() == null || product.getUnit().length() < 1)
            productDash.setVisibility(View.INVISIBLE);

        return rowRootView;
    }

    private void sort() {
        Collections.sort(listItems, new Comparator<Product>() {
            @Override
            public int compare(Product product, Product t1) {
                return product.isSelected().compareTo(t1.isSelected());
            }
        });
        notifyDataSetChanged();
    }
}
