package shoppinglist.android.schumann.max.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import shoppinglist.android.schumann.max.shoppinglist.models.Product;
import shoppinglist.android.schumann.max.shoppinglist.rest.ProductIntentService;

public class ProductActivity extends AppCompatActivity {

    public static final String EXTRA_NEW_PRODUCT_HANDLER = "max.schumann.shoppinglist.EXTRA_NEW_PRODUCT_HANDLER";

    private Messenger receiver;
    private ProductActivity _this = this;

    private EditText productName;
    private EditText productUnit;
    private EditText productQuantity;
    private EditText productListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        receiver = (Messenger) extras.get(EXTRA_NEW_PRODUCT_HANDLER);

        productName     = (EditText)findViewById(R.id.product_name_editText);
        productUnit     = (EditText)findViewById(R.id.product_unit_editText);
        productQuantity = (EditText)findViewById(R.id.product_quantity_editText);
        productListName = (EditText)findViewById(R.id.product_list_name_editText);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_product) {
            if (productName.getText().toString() == null || productName.getText().toString().length() < 1) {
                productName.setError(getString(R.string.product_name_validation));
            } else {
                Double quantity = Double.parseDouble(productQuantity.getText().toString());
                Product newProduct = new Product(
                        productName.getText().toString(),
                        quantity,
                        productUnit.getText().toString(),
                        productListName.getText().toString()
                );
                ProductIntentService.startActionPost(_this, receiver, newProduct);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
