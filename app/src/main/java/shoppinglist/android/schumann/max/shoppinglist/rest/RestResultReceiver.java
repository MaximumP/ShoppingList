package shoppinglist.android.schumann.max.shoppinglist.rest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;

/**
 * Created by max on 12.05.16.
 *
 * Helper class to wrap the ResultReceiver in an interface
 */
public class RestResultReceiver extends ResultReceiver {

    private Receiver receiver = null;

    public static final Parcelable.Creator<RestResultReceiver> CREATOR = null;

    public RestResultReceiver(Handler handler, Receiver receiver) {
        super(handler);
        this.receiver = receiver;
    }

    public interface Receiver {
        void onReceiveResult(int stateCode, Bundle data);
    }

    @Override
    protected void onReceiveResult(int stateCode, Bundle data) {
        receiver.onReceiveResult(stateCode, data);
    }

}
