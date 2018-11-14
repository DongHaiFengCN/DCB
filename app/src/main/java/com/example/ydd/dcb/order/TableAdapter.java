package com.example.ydd.dcb.order;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.lite.save.Save;
import com.example.ydd.dcb.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {


    private ListenerToken listenerToken;
    private Query query;
    private List<String> mData = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.table_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.mText.setText(mData.get(i));

    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    /**
     * 监听 areaId 所属的table
     *
     * @param areaId
     */
    public void startListener(String areaId) {

        query = QueryBuilder.select(
                SelectResult.property("tableId"),
                SelectResult.property("state"))
                .from(DataSource.database(CDLFactory.getInstance().getDatabase()))
                .where(Expression.property("msgTable_areaId")
                        .equalTo(Expression.string(areaId)));


        listenerToken = query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {

                if (mData.size() > 0) {
                    mData.clear();
                }

                ResultSet results = change.getResults();

                Result result;

                while ((result = results.next()) != null) {

                    String id = result.getString("tableId");

                    Document document = CDLFactory.getInstance().getDocument(id);

                    mData.add(document.getString("name"));


                }

                notifyDataSetChanged();


            }
        });

        try {
            query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mText;

        ViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.item_tx);
        }
    }

    /**
     * 销毁页面时移除动态接听
     */
    protected void onDestroy() {


        query.removeChangeListener(listenerToken);

    }


}
