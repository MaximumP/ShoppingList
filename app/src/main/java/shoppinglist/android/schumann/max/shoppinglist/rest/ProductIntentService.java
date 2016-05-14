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
 *
 * Offers CRUD methods for product objects
 * <p>
 * helper methods.
 */
public class ProductIntentService extends IntentService {

    // Rest actions
    private static final String ACTION_GET_LIST  = "max.schumann.shoppinglist.rest.product.GET_LIST";
    private static final String ACTION_GET       = "max.schumann.shoppinglist.rest.product.GET";
    private static final String ACTION_POST      = "max.schumann.shoppinglist.rest.product.POST";
    private static final String ACTION_PUT       = "max.schumann.shoppinglist.rest.product.PUT";
    private static final String ACTION_DELETE    = "max.schumann.shoppinglist.rest.product.DELETE";

    // Extra parameter
    public  static final String EXTRA_PRODUCT     = "max.schumann.shoppinglist.rest.product.extra.PRODUCT";
    public  static final String EXTRA_PRODUCTS    = "max.schumann.shoppinglist.rest.product.extra.PRODUCTS";
    private static final String EXTRA_RECEIVER    = "max.schumann.shoppinglist.rest.product.extra.RECEIVER";
    private static final String EXTRA_PRODUCT_ID  = "max.schumann.shoppinglist.rest.product.extra.PRODUCT_ID";
    private static final String EXTRA_LIST_LIMIT  = "max.schumann.shoppinglist.rest.product.extra.LIST_LIMIT";

    //Http methods
    private static final String HTTP_GET    = "GET";
    private static final String HTTP_POST   = "POST";
    private static final String HTTP_PUT    = "PUT";
    private static final String HTTP_DELETE = "DELETE";

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
    public static void startActionPut(Context context, Messenger receiver, Product product) {
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
     * @param product: the product to be deleted
     */
    public static void startActionDelete(Context context, Messenger receiver, Product product) {
        Intent intent = new Intent(context, ProductIntentService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_PRODUCT, product);
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
                    handleActionGetList(listLimit, receiver);
                    break;
                case ACTION_GET:
                    final int productIdGet = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
                    break;
                case ACTION_POST:
                    final Product productPost = intent.getParcelableExtra(EXTRA_PRODUCT);
                    handleActionPost(productPost, receiver);
                    break;
                case ACTION_PUT:
                    final Product productPut = intent.getParcelableExtra(EXTRA_PRODUCT);
                    handleActionPut(productPut, receiver);
                    break;
                case ACTION_DELETE:
                    final Product productDelete = intent.getParcelableExtra(EXTRA_PRODUCT);
                    handleActionDelete(productDelete, receiver);
                    break;
                default:
                    Log.w("ProductIntentService", "Unknown intent action: " + action);
                    break;
            }
        }
    }

    /**
     * Gets the product list from the server
     * @param listLimit: max. items return from the server
     * @param receiver: the response receiver
     */
    private void handleActionGetList(int listLimit, Messenger receiver) {
        try {
            URL    url      = new URL(UrlValues.SHOPPING_LIST);
            String response = executeHttpRequest(url, HTTP_GET);
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

    /**
     * Creates a new product
     * @param product to be created
     * @param receiver: the response receiver
     */
    private void handleActionPost(Product product, Messenger receiver) {
        Gson    gson        = new Gson();
        String  jsonProduct = gson.toJson(product);
        Bundle  bundle      = new Bundle();
        Message message     = Message.obtain();

        try {
            URL     url        = new URL(UrlValues.SHOPPING_LIST);
            String  response   = executeHttpRequest(url, HTTP_POST, jsonProduct);
            Product newProduct = gson.fromJson(response, Product.PRODUCT_TYPE);

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
     * Updates a product
     * @param product: the updated product model
     * @param receiver: the response receiver
     */
    private void handleActionPut(Product product, Messenger receiver) {
        Gson    gson        = new Gson();
        String  jsonProduct = gson.toJson(product);
        Bundle  bundle      = new Bundle();
        Message message     = Message.obtain();

        try {
            URL     url            = new URL(UrlValues.SHOPPING_LIST);
            String  response       = executeHttpRequest(url, HTTP_PUT, jsonProduct);
            Product updatedProduct = gson.fromJson(response, Product.PRODUCT_TYPE);

            bundle.putParcelable(EXTRA_PRODUCT, updatedProduct);
            message.setData(bundle);
            receiver.send(message);

        } catch (MalformedURLException e) {
            Log.e("ProductIntentService", "Malformed Url", e);
        } catch (RemoteException e) {
            Log.e("ProductIntentService", "Cannot sent result message to the receiver", e);
        }
    }

    /**
     * Deletes a product from the server list
     * @param product to be deleted
     * @param receiver: the response receiver
     */
    private void handleActionDelete(Product product, Messenger receiver) {
        long id = product.getId();
        Bundle bundle = new Bundle();
        Message message = Message.obtain();
        HttpURLConnection con = null;

        try {
            String sUrl = UrlValues.SHOPPING_LIST + "/" + id;
            URL url = new URL(sUrl);
            //TODO: edit execute method so the response code can be checked
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestMethod(HTTP_DELETE);
            con.connect();
            int responseCode = con.getResponseCode();
            if (responseCode == 204) {
                bundle.putParcelable(EXTRA_PRODUCT, product);
            } else {
                bundle.putParcelable(EXTRA_PRODUCT, null);
            }
            message.setData(bundle);
            receiver.send(message);

        } catch (MalformedURLException e) {
            Log.e("ProductIntentService", "Malformed Url", e);
        } catch (RemoteException e) {
            Log.e("ProductIntentService", "Cannot sent result message to the receiver", e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }
    }

    /**
     * Executes a http request
     *
     * @param url: the target url
     * @param requestMethod: the http method to be executed
     * @return the http response body as a json string
     */
    private String executeHttpRequest(URL url, String requestMethod) { return executeHttpRequest(url, requestMethod, null); }

    /**
     *
     * @param url: the target url
     * @param requestMethod: the http method to be executed
     * @param body: the request body
     * @return the http response as a json string
     */
    private String executeHttpRequest(URL url, String requestMethod, String body){

        HttpURLConnection urlConnection    = null;
        BufferedReader    bufferedReader   = null;
        String            jsonResponse     = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(requestMethod);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            if (body != null) {
                OutputStream os = urlConnection.getOutputStream();
                os.write(body.getBytes("UTF-8"));
                os.close();
            }

            urlConnection.connect();
            if (requestMethod.equals("DELETE"))
                return null;

            int responseCode = urlConnection.getResponseCode();

            InputStream  inputStream = urlConnection.getInputStream();
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
