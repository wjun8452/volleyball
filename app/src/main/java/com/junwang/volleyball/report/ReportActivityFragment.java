package com.junwang.volleyball.report;

import android.content.ClipData;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.junwang.volleyball.R;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.stat.StatFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReportActivityFragment extends Fragment implements ReportContract.View {

    private ReportContract.Presenter presenter;
    private ListViewAdapter adapter;
    private View root;

    public ReportActivityFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_frag, container, false);

        List<ReportItem> reportItems = new ArrayList<>();
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));
//        reportItems.add(new ReportItem("王军", 1, 1, 1, 1, 1, 1, 1, 1, 1));

        ListView listView = (ListView) view.findViewById(R.id.listview);

        adapter = new ListViewAdapter(reportItems);

        listView.setAdapter(adapter);

        root = view;

        return view;
    }

    @Override
    public void setPresenter(ReportContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showReport(List<ReportItem> reportItemList) {
        adapter.replaceData(reportItemList);
    }


    @Override
    public void showCreatedTime(String id) {
        ((TextView) root.findViewById(R.id.createdTime)).setText(id);
    }

    @Override
    public void updateCourtStatus(CourtStatus courtStatus) {
        ((TextView) root.findViewById(R.id.status)).setText(courtStatus.name());
    }

    @Override
    public void showScore(int mine, int yours) {
        ((TextView) root.findViewById(R.id.my_score)).setText(Integer.valueOf(mine).toString());
        ((TextView) root.findViewById(R.id.your_score)).setText(Integer.valueOf(yours).toString());
    }

    private static class ListViewAdapter extends BaseAdapter {
        private final int HEADER_ROW_COUNT = 1;
        private List<ReportItem> list;

        public ListViewAdapter(List<ReportItem> list) {
            this.list = list;
        }

        public void replaceData(List<ReportItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size() + HEADER_ROW_COUNT;
        }

        @Override
        public Object getItem(int i) {
            if (i < HEADER_ROW_COUNT) {
                return null;
            } else {
                return list.get(i - HEADER_ROW_COUNT);
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.item_report, viewGroup, false);
            }

            if (i >= HEADER_ROW_COUNT) {
                rowView.setBackgroundColor(Color.TRANSPARENT);

                ReportItem item = (ReportItem) getItem(i);
                ((TextView) rowView.findViewById(R.id.name)).setText(item.getPlayer());
                ((TextView) rowView.findViewById(R.id.net_score)).setText(
                        "+"
                                + Integer.valueOf(item.getWin_score()).toString()
                                + "/-"
                                + Integer.valueOf(item.getLost_score()).toString()
                );

                if (item.getWin_score() > item.getLost_score()) {
                    rowView.setBackgroundColor(Color.GREEN);
                } else if (item.getWin_score() < item.getLost_score()) {
                    rowView.setBackgroundColor(Color.RED);
                } else {
                    rowView.setBackgroundColor(Color.TRANSPARENT);
                }

                ((TextView) rowView.findViewById(R.id.jingong)).setText(
                        "+"
                                + Integer.valueOf(item.getWin_jingong()).toString()
                                + "/-"
                                + Integer.valueOf(item.getLost_jingong()).toString());

                ((TextView) rowView.findViewById(R.id.faqiu)).setText("+"
                        + Integer.valueOf(item.getWin_faqiu()).toString()
                        + "/-"
                        + Integer.valueOf(item.getLost_faqiu()).toString());

                ((TextView) rowView.findViewById(R.id.lanwang)).setText(
                        "+"
                                + Integer.valueOf(item.getWin_lanqang()).toString()
                                + "/-"
                                + Integer.valueOf(item.getLost_lanwang()).toString());

                ((TextView) rowView.findViewById(R.id.yichuan)).setText("-"+Integer.valueOf(item.getLost_yichuan()).toString());

                ((TextView) rowView.findViewById(R.id.fangshou)).setText("-"+Integer.valueOf(item.getLost_fangshou()).toString());

            } else {
                rowView.setBackgroundColor(Color.GRAY);

                ((TextView) rowView.findViewById(R.id.name)).setText("姓名");
                ((TextView) rowView.findViewById(R.id.net_score)).setText("得分");
                ((TextView) rowView.findViewById(R.id.jingong)).setText("进攻");
                ((TextView) rowView.findViewById(R.id.lanwang)).setText("拦网");
                ((TextView) rowView.findViewById(R.id.faqiu)).setText("发球");
                ((TextView) rowView.findViewById(R.id.yichuan)).setText("一传");
                ((TextView) rowView.findViewById(R.id.fangshou)).setText("防守");
            }

            return rowView;
        }
    }
}
