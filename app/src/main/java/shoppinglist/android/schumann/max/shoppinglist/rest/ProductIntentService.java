package shoppinglist.android.schumann.max.shoppinglist.rest;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ProductIntentService extends IntentService {

    // Rest actions
    private static final String ACTION_GET_LIST = "max.schumann.shoppinglist.rest.product.GETLIST";
    private static final String ACTION_GET       = "max.schumann.shoppinglist.rest.product.GET";
    private static final String ACTION_POST      = "max.schumann.shoppinglist.rest.product.POST";
    private static final String ACTION_PUT       = "max.schumann.shoppinglist.rest.product.PUT";
    private static final String ACTION_DELETE = "max.schumann.shoppinglist.rest.product.DELETE";

    // Extra parameter
    private static final String EXTRA_PRODUCT    = "max.schumann.shoppinglist.rest.product.extra.PRODUCT";
    private static final String EXTRA_RECEIVER   = "max.schumann.shoppinglist.rest.product.extra.RECEIVER";
    //private static final String EXTRA_RESULT     = "max.schumann.shoppinglist.rest.product.extra.RESULT";
    private static final String EXTRA_PRODUCT_ID = "max.schumann.shoppinglist.rest.product.extra.PRODUCT_ID";
    private static final String EXTRA_LIST_LIMIT = "max.schumann.shoppinglist.rest.product.extra.LIST_LIMIT";

    public ProductIntentService() {
        super("ProductIntentService");
    }

    /**
     * Get a list of product from the Server
     *
     * @param context: the context in which the intent is performed in
     * @param receiver: the receiver of the server response
     * @param listLimit: max list items to get from the server
     */
    public static void startActionGetList(Context context, ResultReceiver receiver, Integer listLimit) {
        Intent intent = new Intent(context, ProductIntentService.class);
        intent.setAction(ACTION_GET_LIST);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_LIST_LIMIT, listLimit);
        context.startService(intent);
    }

    /**
     * Gets a single product from the server
     *
     * @param context: the context in which the intent is performed in
     * @param receiver: the receiver of the server response
     * @param productId: the id of the product to fetch
     */
    public static void startActionGet(Context context, ResultReceiver receiver, Integer productId) {
        Intent intent = new Intent(context, ProductIntentService.class);
        intent.setAction(ACTION_GET);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        context.startService(intent);
    }

    /**
     * Posts a new product to the server
     *
     * @param context: the context in which the intent is performed in
     * @param receiver: the receiver of the server response
     * @param product: the product to be created
     */
    //TODO: replace "String product" with actual model
    public static void startActionPost(Context context, ResultReceiver receiver, String product) {
        Intent intent = new Intent(context, ProductIntentService.class);
        intent.setAction(ACTION_POST);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_PRODUCT, product);
        context.startService(intent);
    }

    /**
     * Puts(updates) a product
     *
     * @param context: the context in which the intent is performed in
     * @param receiver: the receiver of the server response
     * @param product: the product to be created
     */
    //TODO: replace "String product with actual model
    public static void startActionPut(Context context, ResultReceiver receiver, String product) {
        Intent intent = new Intent(context, ProductIntentService.class);
        intent.setAction(ACTION_PUT);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_PRODUCT, product);
        context.startService(intent);
    }

    /**
     * Deletes a product
     *
     * @param context: the context in which the intent is performed in
     * @param receiver: the receiver of the server response
     * @param productId: the product to be deleted
     */
    public static void startActionDelete(Context context, ResultReceiver receiver, Integer productId) {
        Intent intent = new Intent(context, ProductIntentService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
            switch (action){
                case ACTION_GET_LIST:
                    final int listLimit = intent.getIntExtra(EXTRA_LIST_LIMIT, 50);
                    break;
                case ACTION_GET:
                    final int productIdGet = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
                    break;
                case ACTION_POST:
                    //TODO: replace with model
                    final String productPost = intent.getStringExtra(EXTRA_PRODUCT);
                    break;
                case ACTION_PUT:
                    final String productPut = intent.getStringExtra(EXTRA_PRODUCT);
                    break;
                case ACTION_DELETE:
                    final int productIdDelete = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
                    break;
                default:
                    Log.w("Unknown action", "ProductIntentService: unknown intent action: " + action);
                    break;
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Bundle executeHttpRequest(URL url, String requestMethod){

        HttpURLConnection urlConnection    = null;
        BufferedReader    bufferedReader   = null;
        String            jsonResponse     = null;



        return null;
    }
}
