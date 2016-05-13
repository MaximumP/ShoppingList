package shoppinglist.android.schumann.max.shoppinglist.rest;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import shoppinglist.android.schumann.max.shoppinglist.StringValue.UrlValues;
import shoppinglist.android.schumann.max.shoppinglist.models.Product;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ProductIntentService extends IntentService {

    // Rest actions
    private static final String ACTION_GET_LIST = "max.schumann.shoppinglist.rest.product.GET_LIST";
    private static final String ACTION_GET       = "max.schumann.shoppinglist.rest.product.GET";
    private static final String ACTION_POST      = "max.schumann.shoppinglist.rest.product.POST";
    private static final String ACTION_PUT       = "max.schumann.shoppinglist.rest.product.PUT";
    private static final String ACTION_DELETE = "max.schumann.shoppinglist.rest.product.DELETE";

    // Extra parameter
    public  static final String EXTRA_PRODUCT     = "max.schumann.shoppinglist.rest.product.extra.PRODUCT";
    public  static final String EXTRA_PRODUCTS    = "max.schumann.shoppinglist.rest.product.extra.PRODUCTS";
    private static final String EXTRA_RECEIVER    = "max.schumann.shoppinglist.rest.product.extra.RECEIVER";
    private static final String EXTRA_PRODUCT_ID  = "max.schumann.shoppinglist.rest.product.extra.PRODUCT_ID";
    private static final String EXTRA_LIST_LIMIT  = "max.schumann.shoppinglist.rest.product.extra.LIST_LIMIT";

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
    public static void startActionGetList(Context context, Messenger receiver, Integer listLimit) {
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
    public static void startActionGet(Context context, Messenger receiver, Integer productId) {
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
    public static void startActionPost(Context context, Messenger receiver, Product product) {
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
    public static void startActionPut(Context context, Messenger receiver, String product) {
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
    public static void startActionDelete(Context context, Messenger receiver, Integer productId) {
        Intent intent = new Intent(context, ProductIntentService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final Bundle bundle = intent.getExtras();
            final String action = intent.getAction();
            final Messenger receiver = (Messenger) bundle.get(EXTRA_RECEIVER);
            switch (action){
                case ACTION_GET_LIST:
                    final int listLimit = intent.getIntExtra(EXTRA_LIST_LIMIT, 50);
                    handleActionGetList(50, receiver);
                    break;
                case ACTION_GET:
                    final int productIdGet = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
                    break;
                case ACTION_POST:
                    final Product productPost = intent.getParcelableExtra(EXTRA_PRODUCT);
                    handleActionPost(productPost, receiver);
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

    private void handleActionGetList(int listLimit, Messenger receiver) {
        try {
            URL    url      = new URL(UrlValues.SHOPPING_LIST);
            String response = executeHttpRequest(url, "GET");
            Bundle bundle   = new Bundle();
            Message message = Message.obtain();

            Gson gson = new Gson();
            Type type = new TypeToken<List<Product>>() {}.getType();
            ArrayList<Product> products = gson.fromJson(response, type);

            bundle.putParcelableArrayList(EXTRA_PRODUCTS, products);
            message.setData(bundle);
            receiver.send(message);

        } catch (MalformedURLException e) {
            Log.e("ProductIntentService", "Malformed Url", e);
        } catch (RemoteException e) {
            Log.e("ProductIntentService", "Cannot sent result message to the receiver", e);
        }
    }

    private void handleActionPost(Product product, Messenger receiver) {
        Gson    gson        = new Gson();
        String  jsonProduct = gson.toJson(product);
        Bundle  bundle      = new Bundle();
        Message message     = Message.obtain();

        try {
            URL url = new URL(UrlValues.SHOPPING_LIST);
            String response = executeHttpRequest(url, "POST", jsonProduct);
            Type type = new TypeToken<Product>() {}.getType();
            Product newProduct = gson.fromJson(response, type);

            bundle.putParcelable(EXTRA_PRODUCT, newProduct);
            message.setData(bundle);
            receiver.send(message);

        } catch (MalformedURLException e) {
            Log.e("ProductIntentService", "Malformed Url", e);
        } catch (RemoteException e) {
            Log.e("ProductIntentService", "Cannot sent result message to the receiver", e);
        }
    }

    /**
     * Executes a http request
     *
     * @param url: the target url
     * @param requestMethod: the http method to be exected
     * @return the http response body
     */
    private String executeHttpRequest(URL url, String requestMethod) { return executeHttpRequest(url, requestMethod, null); }
    private String executeHttpRequest(URL url, String requestMethod, String body){

        HttpURLConnection urlConnection    = null;
        BufferedReader    bufferedReader   = null;
        String            jsonResponse     = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(requestMethod);

            if (body != null) {
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write(body.getBytes("UTF-8"));
                os.close();
            }

            urlConnection.connect();


            InputStream  inputStream  = urlConnection.getInputStream();
            StringBuilder stringBuffer = new StringBuilder();

            if (inputStream == null) {
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            if (stringBuffer.length() == 0){
                return null;
            }

            jsonResponse = stringBuffer.toString();
        } catch (IOException e) {
            Log.e("ProductIntentService", "Error", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e("ProductIntentService", "Error closing stream", e);
                }
            }
        }

        return jsonResponse;
    }
}
