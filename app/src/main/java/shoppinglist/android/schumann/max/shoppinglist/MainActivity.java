package shoppinglist.android.schumann.max.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import shoppinglist.android.schumann.max.shoppinglist.adapter.ShoppingListAdapter;
import shoppinglist.android.schumann.max.shoppinglist.models.Product;
import shoppinglist.android.schumann.max.shoppinglist.rest.ProductIntentService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ShoppingListAdapter shoppingList;
    private Context             context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shoppingList = new ShoppingListAdapter(this);
        ListView shoppingListListView = (ListView) findViewById(R.id.shopping_list_listview);
        shoppingListListView.setOnItemClickListener(listItemOnClickListener);
        shoppingListListView.setAdapter(shoppingList);
        ProductIntentService.startActionGetList(this, new Messenger(getListHandler), 12);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            final Intent productActivityIntent = new Intent(this, ProductActivity.class);
            productActivityIntent.putExtra(ProductActivity.EXTRA_PRODUCT_HANDLER, new Messenger(newProductHandler));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(productActivityIntent);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        shoppingList.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove_shopping_cart) {
            ArrayList<Product> selected = shoppingList.getSelectedItems();
            for (Product product: selected) {
                ProductIntentService.startActionDelete(this, new Messenger(deleteProductHandler), product);
            }
            return true;
        } else if (id == R.id.action_refresh_list) {
            ProductIntentService.startActionGetList(this, new Messenger(getListHandler), 12);
            Toast.makeText(
                    this,
                    getResources().getText(R.string.toast_list_updated),
                    Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Handler getListHandler = new Handler() {
        @Override
        public void handleMessage(Message result) {
            Bundle data = result.getData();

            ArrayList<Product> products =
                    data.getParcelableArrayList(ProductIntentService.EXTRA_PRODUCTS);

            if (products != null) {
                shoppingList.setListItems(products);
            }
        }
    };

    private Handler newProductHandler = new Handler() {
        @Override
        public void handleMessage(Message result) {
            Bundle data = result.getData();

            Product newProduct = data.getParcelable(ProductIntentService.EXTRA_PRODUCT);
            if (newProduct != null) {
                shoppingList.add(newProduct);
            }
        }
    };

    private Handler updateProductHandler = new Handler(){
        @Override
        public void handleMessage(Message result) {
            Bundle data = result.getData();

            Product product = data.getParcelable(ProductIntentService.EXTRA_PRODUCT);
            shoppingList.updateProduct(product);
        }
    };

    private Handler deleteProductHandler = new Handler() {
        @Override
        public void handleMessage(Message result) {
            Bundle data = result.getData();
            Product product = data.getParcelable(ProductIntentService.EXTRA_PRODUCT);
            shoppingList.remove(product.getId());
        }
    };

    private AdapterView.OnItemClickListener listItemOnClickListener = new AdapterView
            .OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Product product = shoppingList.getItem(position);
            final Intent intent = new Intent(context, ProductActivity.class);
            intent.putExtra(ProductActivity.EXTRA_PRODUCT_HANDLER, new Messenger(updateProductHandler));
            intent.putExtra(ProductActivity.EXTRA_PRODUCT, product);
            startActivity(intent);
        }
    };
}
