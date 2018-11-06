package com.example.ydd.common.lite.common;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Endpoint;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * @author dong
 * @date 2018/11/03
 */
public class CDLFactory {

    public static Database database;

    private LoginChangerListener loginChangerListener;

    public void setLoginChangerListener(LoginChangerListener loginChangerListener) {
        this.loginChangerListener = loginChangerListener;
    }

    public void initCouchBaseLite(Context context) {

        DatabaseConfiguration config = new DatabaseConfiguration(context.getApplicationContext());

        try {
            database = new Database("Local_dcb", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     * @param channels 同步的通道
     * @param adminPsw 网关用户
     * @param adminName 暂时和channel一个
     */

    public void startReplicator(List<String> channels, String adminPsw, String adminName) {

        Endpoint targetEndpoint = null;
        try {
            targetEndpoint = new URLEndpoint(new URI("ws://123.207.174.171:4984/kitchen/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(database, targetEndpoint)
                .setContinuous(true)
                .setReplicatorType(ReplicatorConfiguration.ReplicatorType.PULL)
                .setAuthenticator(new BasicAuthenticator(adminName, adminPsw));


        Replicator replicator = new Replicator(replConfig);

        replicator.addChangeListener(new ReplicatorChangeListener() {
            @Override
            public void changed(ReplicatorChange change) {

                if (loginChangerListener != null) {

                    loginChangerListener.getProgress(change.getStatus().getProgress().getCompleted()
                            , change.getReplicator().getStatus().getProgress().getTotal());
                }

                if (change.getStatus().getError() != null)
                    Log.i(TAG, "Error code ::  " + change.getStatus().getError().toString());
            }
        });

        replicator.start();


    }


    public interface LoginChangerListener {

        void getProgress(Long completed, Long total);
    }
}
