package com.example.ydd.common.lite.query;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.example.ydd.common.lite.common.CBLite;
import com.example.ydd.common.lite.common.CDLFactory;

public class QueryWithId extends CBLite {

    private Database database = CDLFactory.database;

    private static QueryWithId queryWithSingleConditional;


    private String id;

    public QueryWithId setId(String id) {

        this.id = id;
        return this;
    }


    @Override
    public Document generate() {


        return CDLFactory.database.getDocument(id);
    }

    public static QueryWithId getInstance() {

        if (queryWithSingleConditional == null) {

            queryWithSingleConditional = new QueryWithId();
        }
        return queryWithSingleConditional;
    }


}
