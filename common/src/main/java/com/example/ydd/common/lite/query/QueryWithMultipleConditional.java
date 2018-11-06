package com.example.ydd.common.lite.query;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.From;
import com.couchbase.lite.Meta;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CBLite;
import com.example.ydd.common.lite.common.CDLFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class QueryWithMultipleConditional extends CBLite {
    private Database database = CDLFactory.database;
    private static QueryWithMultipleConditional queryWithMultipleConditional;


    private static HashMap<String, Object> hashMap;

    public QueryWithMultipleConditional addConditional(String k, Object v) {

        hashMap.put(k, v);

        return this;
    }


    /**
     * 通过map获取参数拼接查询请求，任意长度的参数，and的形式查询。。。。
     *
     * @return 返回List<Dictionary>
     */

    @Override
    public Object generate() {

        getExpression();

        From from = QueryBuilder.select(SelectResult.expression(Meta.id),SelectResult.all()).from(DataSource.database(database));
        ResultSet results = null;
        try {

            Expression expression = getExpression();

            results = from.where(expression).execute();

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        assert results != null;
        ArrayList<Dictionary> dictionaries = new ArrayList<>();
        Result r;
        Dictionary dictionary;
        while ((r = results.next()) != null) {


            dictionary = r.getDictionary(1);

            dictionaries.add(dictionary);

            Log.e("DOAING", r.getString(0));
            Log.e("DOAING", dictionary.getString("name"));


        }
        //15688882487 mobile
        return null;
    }

    private Expression getExpression() {

        Iterator entries = hashMap.entrySet().iterator();

        Map.Entry entry = (Map.Entry) entries.next();

        Expression expression = Expression.property(String.valueOf(entry.getKey())).equalTo(Expression.value(entry.getValue()));

        while (entries.hasNext()) {

            entry = (Map.Entry) entries.next();

            expression.and(Expression.string(String.valueOf(entry.getKey())).equalTo(Expression.value(entry.getValue())));

        }

        return expression;
    }

    public static QueryWithMultipleConditional getInstance() {

        if (queryWithMultipleConditional == null) {

            queryWithMultipleConditional = new QueryWithMultipleConditional();

            hashMap = new HashMap<>();

        } else {

            hashMap.clear();
        }

        return queryWithMultipleConditional;

    }


}
