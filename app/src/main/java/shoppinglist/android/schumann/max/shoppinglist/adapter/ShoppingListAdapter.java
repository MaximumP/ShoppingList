package shoppinglist.android.schumann.max.shoppinglist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import shoppinglist.android.schumann.max.shoppinglist.R;
import shoppinglist.android.schumann.max.shoppinglist.models.Product;

/**
 * Created by max on 13.05.16.
 * <p/>
 * Custom adapter for the shopping list view
 */
public class ShoppingListAdapter extends BaseAdapter {

    private       ArrayList<Product> listItems;
    private final Context            context;

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

    public void remove(long id) {
        int index = getIndexFromId(id);
        if (index != -1)
            listItems.remove(index);
        super.notifyDataSetChanged();
    }

    public ArrayList<Product> getSelectedItems() {
        ArrayList<Product> selected = new ArrayList<>();
        for (Product product : listItems) {
            if (product.isSelected()) {
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

        final Product product = listItems.get(position);
        ViewHolder holder;
        View vi = view;
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            vi = inflater.inflate(R.layout.shopping_list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.product_name_textview);
            holder.quantity = (TextView) vi.findViewById(R.id.product_quantity_textview);
            holder.dash = (TextView) vi.findViewById(R.id.product_dash_textview);
            holder.unit = (TextView) vi.findViewById(R.id.product_unit_textview);
            holder.checkBox = (CheckBox) vi.findViewById(R.id.product_shoppingcart_checkbox);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.name.setText(product.getName());
        holder.quantity.setText(String.format(
                context.getResources().getConfiguration().locale,
                product.getQuantity().toString()));
        holder.unit.setText(product.getUnit());
        holder.checkBox.setChecked(product.isSelected());

        if (product.getUnit() == null || product.getUnit().length() < 1)
            holder.dash.setVisibility(View.INVISIBLE);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                product.setSelected(b);
                sort();
            }
        });


        return vi;
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

    private int getIndexFromId(long id) {
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    public void updateProduct(Product product) {
        int index = getIndexFromId(product.getId());
        if (index != -1) {
            Product oldProduct = getItem(index);
            oldProduct.update(product);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        public TextView name;
        public TextView quantity;
        public TextView dash;
        public TextView unit;
        public CheckBox checkBox;
    }
}
