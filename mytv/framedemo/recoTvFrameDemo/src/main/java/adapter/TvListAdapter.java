package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import reco.frame.demo.R;
import reco.frame.demo.entity.AppInfo;
import reco.frame.tv.view.component.TvBaseAdapter;

public class TvListAdapter extends TvBaseAdapter {


    private List<AppInfo> appList;
    private LayoutInflater inflater;
    private Context context;

    public TvListAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.appList = appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder = null;
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.btn_name = (Button) contentView.findViewById(R.id.btn_name);

            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        AppInfo app = appList.get(position);
        holder.btn_name.setText(app.title);


        return contentView;
    }

    public void addItem(AppInfo item) {
        appList.add(item);
    }

    public void clear() {
        appList.clear();
    }

    public void flush(List<AppInfo> appListNew) {
        appList = appListNew;
    }


    static class ViewHolder {
        Button btn_name;

    }

    public void show() {


    }
}
