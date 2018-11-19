package com.example.ydd.dcb.order;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.OrderBy;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Ordering.SortOrder;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.dcb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {


    private ListenerToken listenerToken;
    private Query query;
    private List<Result> mData = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.table_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.mText.setText("状态 " + mData.get(i).getInt("state") + "\n 桌号：" + mData.get(i).getInt("serialNumber"));
        viewHolder.mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO 这里网络访问获取服务器的餐桌的状态，保证数据唯一


                String id = mData.get(i).getString(0);
                MutableDocument document = CDLFactory.getInstance()
                        .getDocument(id).toMutable();

                if (document.getInt("state") == 0) {

                    document.setInt("state", 1);

                } else {

                    document.setInt("state", 0);

                }

                CDLFactory.getInstance().saveDocument(document);

            }
        });

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
                SelectResult.expression(Meta.id),
                SelectResult.property("state"),
                SelectResult.property("serialNumber"),
                SelectResult.property("valid")

        )
                .from(DataSource.database(CDLFactory.getInstance().getDatabase()))
                .where(Expression.property("areaId")
                        .equalTo(Expression.string(areaId)))
                .orderBy(SortOrder.expression(Expression.property("serialNumber"))
                        .ascending());


        listenerToken = query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {


                List<Result> resultList = change.getResults().allResults();
                int size = resultList.size();
                Result result;

                for (int i = 0; i < size; i++) {


                    result = resultList.get(i);


                    goChange(result, i);

                }

            }
        });

        try {
            query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void goChange(Result result, int i) {

        String id = result.getString("id");

        int state = result.getInt("state");

        boolean valid = result.getBoolean("valid");

        int serialNumber = result.getInt("serialNumber");

        int count = 0;

        if (!contains(result) && valid) {//添加新餐桌并排序

            for (Result old : mData) {

                if (serialNumber < old.getInt("serialNumber")) {

                    break;

                }

                count++;
            }

            mData.add(count, result);

            notifyItemInserted(count);

            notifyItemRangeChanged(count, mData.size());


        } else if (contains(result)) {


            for (Result old : mData) {

                if (id.equals(old.getString("id"))) {

                    if (state != old.getInt("state")) {

                        mData.set(count, result);

                        notifyItemChanged(count);

                        return;

                    } else if (valid != old.getBoolean("valid")) {

                        mData.remove(count);

                        notifyItemRemoved(count);

                        notifyItemRangeChanged(count, mData.size());

                        return;

                    }

                }

                count++;
            }

        }

    }

    private boolean contains(Result result) {

        for (Result result1 :
                mData)

            if (result1.getString("id").equals(result.getString("id"))) {

                return true;
            }

        return false;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mText;

        ViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.item_tx);
        }
    }

    /**
     * 移除动态接听
     */
    protected void onDestroy() {

        query.removeChangeListener(listenerToken);

    }


}
