package shoppinglist.android.schumann.max.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import shoppinglist.android.schumann.max.shoppinglist.models.Product;
import shoppinglist.android.schumann.max.shoppinglist.rest.ProductIntentService;

public class ProductActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_HANDLER = "max.schumann.shoppinglist.EXTRA_PRODUCT_HANDLER";
    public static final String EXTRA_PRODUCT         = "max.schumann.shoppinglist.EXTRA_PRODUCT";

    private Messenger receiver;
    private Product   product;
    private Context   context = this;

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
        receiver = (Messenger) extras.get(EXTRA_PRODUCT_HANDLER);
        product  = (Product) extras.get(EXTRA_PRODUCT);

        productName     = (EditText)findViewById(R.id.product_name_editText);
        productUnit     = (EditText)findViewById(R.id.product_unit_editText);
        //TODO: maybe a spinner is an better option
        productQuantity = (EditText)findViewById(R.id.product_quantity_editText);
        productListName = (EditText)findViewById(R.id.product_list_name_editText);

        if (product != null) {
            productName.setText(product.getName());
            productUnit.setText(product.getUnit());
            productQuantity.setText(product.getQuantity().toString());
            productListName.setText(product.getShop());
        }

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
            //Product name is a mandatory field. Mark text field with error
            if (productName.getText().toString() == null || productName.getText().toString().length() < 1) {
                productName.setError(getString(R.string.product_name_validation));
            } else {
                // Set the quantity to the default 1.0 if the text field is empty
                String sQuantity = productQuantity.getText().toString();
                Double quantity = 1.0;
                if (sQuantity.length() > 0) {
                    quantity = Double.parseDouble(sQuantity);
                }

                // Update a product
                if (product != null && product.getId() != -1L) {
                    product.setName(productName.getText().toString());
                    product.setQuantity(quantity);
                    product.setUnit(productUnit.getText().toString());
                    product.setShop(productListName.getText().toString());
                    ProductIntentService.startActionPut(context, receiver, product);
                } else {
                    product = new Product(
                            productName.getText().toString(),
                            quantity,
                            productUnit.getText().toString(),
                            productListName.getText().toString()
                    );
                    ProductIntentService.startActionPost(context, receiver, product);
                }

                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
