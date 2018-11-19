package com.example.ydd.common.lite.common;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Endpoint;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.ydd.common.tools.Constant.REPLICATE_URL;

/**
 * @author dong
 * @date 2018/11/03
 */
public class CDLFactory {

    private Replicator replicator;

    private static CDLFactory cdlFactory;

    public Database getDatabase() {

        return database;
    }

    private static Database database;

    private LoginChangerListener loginChangerListener;


    public void setLoginChangerListener(LoginChangerListener loginChangerListener) {
        this.loginChangerListener = loginChangerListener;
    }

    public CDLFactory initCouchBaseLite(Context context) {

        DatabaseConfiguration config = new DatabaseConfiguration(context.getApplicationContext());

        try {
            database = new Database("Local_dcb", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return this;
    }


    public void startReplicator(String[] ss) {

        List<String> channels = new ArrayList<>();

        channels.add(ss[0]);

        String adminPsw = ss[1];
        String adminName = ss[2];

        Endpoint targetEndpoint = null;
        try {
            targetEndpoint = new URLEndpoint(new URI(REPLICATE_URL));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(database, targetEndpoint)
                .setContinuous(true)
                .setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL)
                .setAuthenticator(new BasicAuthenticator(adminName, adminPsw))
                .setChannels(channels);


        replicator = new Replicator(replConfig);

        replicator.addChangeListener(new ReplicatorChangeListener() {
            @Override
            public void changed(ReplicatorChange change) {

                if (loginChangerListener != null) {

                    loginChangerListener.getProgress(change.getStatus()
                                    .getProgress()
                                    .getCompleted()
                            , change.getReplicator().getStatus().getProgress().getTotal());
                }

                if (change.getStatus().getError() != null)
                    Log.i(TAG, "Error code ::  " + change.getStatus().getError().toString());
            }
        });

        replicator.start();
        test();

    }


    public static CDLFactory getInstance() {

        if (cdlFactory == null) {

            cdlFactory = new CDLFactory();
        }

        return cdlFactory;
    }

    public Document getDocument(String id) {


        return database.getDocument(id);
    }

    public void saveDocument(MutableDocument mutableDocument) {


        try {
            database.save(mutableDocument);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void deleteDocument(String id) {

        try {
            database.delete(database.getDocument(id));
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
    public void test() {

        database.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(DatabaseChange change) {


                List<String> ids = change.getDocumentIDs();

                for (String id : ids) {

                    Log.e("DOAING", id);

                  /*  Document document = database.getDocument("id");

                    HashMap hashMap = (HashMap) document.toMap();

                    Log.e("DOAING",hashMap.toString());*/


                }

            }
        });
    }

    public interface LoginChangerListener {

        void getProgress(Long completed, Long total);
    }
}
