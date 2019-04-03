package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.News;
import reco.frame.demo.R;


/**
 * Created by Administrator on 2017/6/1.
 */

public class NewsAdapter extends BaseAdapter {

    List<News> list;
    ViewHolder holder;
    List<ViewHolder> viewHolders = new ArrayList<ViewHolder>();


    private Context context;
    private SharedPreferences sharedPreferences;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Calendar c = Calendar.getInstance();


    public NewsAdapter(Context context, List<News> vaccines) {
        this.list = vaccines;
        this.context = context;


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        List<News> vs = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_news, null);
            holder = new ViewHolder();

            holder.txt_news_title = (TextView) convertView.findViewById(R.id.txt_news_title);
            holder.txt_news_date = (TextView) convertView.findViewById(R.id.txt_news_date);
            holder.imgshow = (ImageView) convertView.findViewById(R.id.imgshow);

            viewHolders.add(holder);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        sharedPreferences = context.getSharedPreferences("User", Activity.MODE_PRIVATE);
        final String title = list.get(position).title;
        String datetime = list.get(position).datetime;

        holder.txt_news_title.setText(title);
        holder.txt_news_date.setText(datetime);


        return convertView;
    }


    class ViewHolder {
        TextView txt_news_title;
        TextView txt_news_date;
        ImageView imgshow;

    }
}
