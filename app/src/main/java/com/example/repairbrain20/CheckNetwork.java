package com.example.repairbrain20;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class CheckNetwork extends ConnectivityManager.NetworkCallback {

    Activity act;
    Snackbar snack;

    CheckNetwork(Activity act,View view)
    {
        this.act = act;
        this.snack = Snackbar.make(view,"Network Not Available",Snackbar.LENGTH_INDEFINITE);

        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(ConnectivityManager.class);
        Network active = cm.getActiveNetwork();
        if(active==null)
        {
            snack.show();
        }
    }
    @Override
    public void onAvailable(@NonNull Network network) {
       // this.snack.dismiss();
        super.onAvailable(network);
    }

    @Override
    public void onLost(@NonNull Network network) {
     //   this.snack.show();
        super.onLost(network);
    }

    @Override
    public void onUnavailable() {
       // this.snack.show();
        super.onUnavailable();
    }

    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
       // this.snack.show();
        super.onLosing(network, maxMsToLive);
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
    }

    @Override
    public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
    }

    @Override
    public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
        super.onBlockedStatusChanged(network, blocked);
    }

    public static boolean isAvailable(Activity act,View view)
    {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(ConnectivityManager.class);
        Network active = cm.getActiveNetwork();

        if(active==null)
        {
            Snackbar bar = Snackbar.make(view,"Network Not Available",BaseTransientBottomBar.LENGTH_INDEFINITE);
            bar.setAction("Reload", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.recreate();
                }
            });

            bar.show();
        }

        return active!=null;
    }

}
