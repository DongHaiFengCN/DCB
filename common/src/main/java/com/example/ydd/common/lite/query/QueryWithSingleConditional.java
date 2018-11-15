package com.example.ydd.common.lite.query;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CBLite;
import com.example.ydd.common.lite.common.CDLFactory;

import java.util.ArrayList;
import java.util.List;

public class QueryWithSingleConditional<K, V> extends CBLite {

    private Database database = CDLFactory.getInstance().getDatabase();

    private static QueryWithSingleConditional queryWithSingleConditional;


    private K k;

    private V v;

    public QueryWithSingleConditional addConditional(K k, V v) {

        this.k = k;
        this.v = v;

        return this;
    }


    @Override
    public List<Dictionary> generate() {

        ResultSet results = null;
        Query query = QueryBuilder.select(SelectResult.all()).from(DataSource.database(database))
                .where(Expression.property(k.toString()).equalTo(Expression.value(v)));

        try {
            results = query.execute();

        } catch (CouchbaseLiteException e1) {
            e1.printStackTrace();
        }

        assert results != null;
        ArrayList<Dictionary> dictionaries = new ArrayList<>();
        Result r;
        Dictionary dictionary;
        while ((r = results.next()) != null) {

            dictionary = r.getDictionary(0);

            dictionaries.add(dictionary);

            Log.e("DOAING", dictionary.getString("name"));
        }


        return dictionaries;
    }

    public static QueryWithSingleConditional getInstance() {

        if (queryWithSingleConditional == null) {

            queryWithSingleConditional = new QueryWithSingleConditional();
        }
        return queryWithSingleConditional;
    }


}
