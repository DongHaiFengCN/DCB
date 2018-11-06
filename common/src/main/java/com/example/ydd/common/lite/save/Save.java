package com.example.ydd.common.lite.save;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.example.ydd.common.lite.common.CBLite;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.lite.query.QueryWithId;

public class Save extends CBLite {
    private Database database = CDLFactory.database;
    private static Save save;

    private MutableDocument mutableDocument;

    public Save save(MutableDocument mutableDocument){
        this.mutableDocument = mutableDocument;

        return this;
    }

    @Override
    public Object generate() {

        try {
            database.save(mutableDocument);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Save getInstance() {

        if (save == null) {

            save = new Save();
        }
        return save;
    }

}
