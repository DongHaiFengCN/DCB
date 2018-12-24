package com.example.ydd.dcb.order;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Ordering.SortOrder;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.lite.query.QueryWithMultipleConditional;
import com.example.ydd.dcb.R;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.app.Application.getProcessName;
import static com.example.ydd.dcb.application.MainApplication.playSound;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private static final int FREES_STATE = 0;
    private static final int USER_STATE = 1;
    private static final int WECHAT_STATE = 2;
    private static final int BOOK_STATE = 3;


    private Query query;
    private ListenerToken listenerToken;
    private List<Result> mData = new ArrayList<>();
    private List<Result> resultList;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.table_item, viewGroup, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final int state = mData.get(i).getInt("state");

        String id = mData.get(i).getString(0);

        Log.e("DOAING", id + " id");

        Document document = CDLFactory.getInstance()
                .getDocument(id);

        viewHolder.tNameTv.setText(document.getString("name"));

        String info = document.getInt("currentRepastTotal") + "/" + document.getInt("maxRepastTotal");

        viewHolder.tAmountTv.setText(info);

        if (state == FREES_STATE) {

            //空闲
            viewHolder.stateLl.setBackgroundResource(R.drawable.card_free);
            viewHolder.enableBt.setVisibility(View.VISIBLE);
            viewHolder.contentTv.setVisibility(View.GONE);


        } else if (state == USER_STATE) {

            //使用
            viewHolder.stateLl.setBackgroundResource(R.drawable.card_use);
            viewHolder.enableBt.setVisibility(View.GONE);
            viewHolder.contentTv.setVisibility(View.VISIBLE);

         /*   if (MainActivity.timer.get(id) == null) {

                long x = (System.currentTimeMillis() - document.getLong("startTime")) / 60000;

                viewHolder.contentTv.setText("¥" + document.getDouble("orderPrice") + "\n" + "就餐时间(分)：" + "\n" + x);

                MainActivity.timer.put(id, x);

            } else {

                viewHolder.contentTv.setText("¥" + document.getDouble("orderPrice") + "\n" + "就餐时间(分)：" + "\n" );

            }*/

        } else if (state == WECHAT_STATE) {
            viewHolder.stateLl.setBackgroundResource(R.drawable.card_wechat);
            viewHolder.enableBt.setVisibility(View.GONE);
            viewHolder.contentTv.setVisibility(View.VISIBLE);

        } else if (state == BOOK_STATE) {

            viewHolder.stateLl.setBackgroundResource(R.drawable.card_book);
            viewHolder.enableBt.setVisibility(View.VISIBLE);
            viewHolder.contentTv.setVisibility(View.GONE);

        }

        viewHolder.tableLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                order(i);
            }
        });

        viewHolder.tableLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //取消

                free(i);

                return true;
            }
        });
        viewHolder.enableBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playSound();

                //TODO 这里网络访问获取服务器的餐桌的状态，保证数据唯一

                use(i);


            }
        });


    }

    private void use(int i) {

        String id = mData.get(i).getString(0);

        MutableDocument document = CDLFactory.getInstance()
                .getDocument(id).toMutable();

        if (document.getInt("state") == FREES_STATE || document.getInt("state") == BOOK_STATE) {

            document.setInt("state", 1);

            document.setInt("currentRepastTotal", 3);


            document.setLong("startTime", System.currentTimeMillis());


            CDLFactory.getInstance().saveDocument(document);

            EventBus.getDefault().post(id);

        }
    }

    private void order(int i) {
        String id = mData.get(i).getString(0);

        MutableDocument document = CDLFactory.getInstance()
                .getDocument(id).toMutable();
        if (document.getInt("state") == USER_STATE) {
            playSound();

            EventBus.getDefault().post(id);
        }
    }

    private void free(int i) {

        String id = mData.get(i).getString(0);

        MutableDocument document = CDLFactory.getInstance()
                .getDocument(id).toMutable();

        if (document.getInt("state") == USER_STATE || document.getInt("state") == WECHAT_STATE) {

            EventBus.getDefault().post(document);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout tableLl;
        LinearLayout stateLl;
        TextView tNameTv;
        TextView tAmountTv;
        Button enableBt;
        TextView contentTv;

        ViewHolder(View itemView) {
            super(itemView);
            tNameTv = itemView.findViewById(R.id.tName_tv);
            tAmountTv = itemView.findViewById(R.id.tAmount_tv);
            enableBt = itemView.findViewById(R.id.enable_bt);
            stateLl = itemView.findViewById(R.id.title_state_ll);
            tableLl = itemView.findViewById(R.id.table_ll);

            contentTv = itemView.findViewById(R.id.content_tv);


        }
    }

    /**
     * 监听 areaId 所属的table
     *
     * @param areaId
     */
    public void startListener(String areaId) {

        if (areaId == null) {
            return;
        }

        if (mData.size() > 0) {

           mData.clear();

           notifyDataSetChanged();


        }


        removeListenerToken();

        query = QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.property("state"),
                SelectResult.property("serialNumber"),
                SelectResult.property("startTime"),
                SelectResult.property("valid"))
                .from(DataSource.database(CDLFactory.getInstance().getDatabase()))
                .where(Expression.property("areaId")
                        .equalTo(Expression.string(areaId)))
                .orderBy(Ordering.SortOrder.expression(Expression.property("serialNumber"))
                        .ascending());

        listenerToken = query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {

                resultList = change.getResults().allResults();

                int size = resultList.size();

                for (int i = 0; i < size; i++) {

                    goChange(resultList.get(i));

                }

            }
        });

        try {
            query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }

    private void goChange(Result result) {


        String id = result.getString("id");

        int state = result.getInt("state");

        long serviceTime = result.getLong("serviceTime");

        boolean valid = result.getBoolean("valid");

        int serialNumber = result.getInt("serialNumber");

        int count = 0;

        if (!contains(result) && valid) {

            //添加新餐桌并排序
            Log.e("DOAING", "不存在");

            for (Result old : mData) {

                if (serialNumber < old.getInt("serialNumber")) {

                    break;

                }

                count++;
            }

            mData.add(count, result);

            notifyItemInserted(count);

            notifyItemRangeChanged(count, mData.size());

            Log.e("DOAING", "mData " + mData.size());
        } else if (contains(result)) {

            Log.e("DOAING", "存在");
            for (Result old : mData) {

                if (id.equals(old.getString("id"))) {

                    if (state != old.getInt("state")||serviceTime!=old.getLong("serviceTime")) {

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

        for (Result old : mData) {
            if (old.getString("id").equals(result.getString("id"))) {

                return true;
            }
        }

        return false;
    }


    public void removeListenerToken() {

        if (listenerToken != null) {
            query.removeChangeListener(listenerToken);
        }


    }

}
