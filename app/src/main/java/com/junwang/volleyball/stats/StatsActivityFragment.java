package com.junwang.volleyball.stats;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.junwang.volleyball.R;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.prepare.PrepareActivity;
import com.junwang.volleyball.report.ReportActivity;
import com.junwang.volleyball.stat.StatActivity;
import com.junwang.volleyball.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

/**
 * A placeholder fragment containing a simple view.
 */
public class StatsActivityFragment extends Fragment implements StatsContract.View, AdapterView.OnItemClickListener {

    private StatsAdapter adapter;
    private StatsContract.Presenter presenter;
    private View root;

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        adapter = new StatsAdapter(new ArrayList<Court>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_frag, container, false);

        SwipeMenuListView listView = (SwipeMenuListView) view.findViewById(R.id.listview_stats);

        listView.setAdapter(adapter);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_stats);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addNewStats();
            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item = new SwipeMenuItem(getActivity());
                item.setBackground(new ColorDrawable(Color.RED));
                item.setWidth(DisplayUtil.dp2px(getActivity(), 90));
                item.setTitle("删除");
                item.setTitleSize(18);
                item.setTitleColor(Color.WHITE);
                menu.addMenuItem(item);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        presenter.deleteStat((Court) adapter.getItem(position));
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        listView.setOnItemClickListener(this);

        root = view;

        return view;
    }

    @Override
    public void showCourts(List<Court> courtList) {
        root.findViewById(R.id.no_stats).setVisibility(View.GONE);
        root.findViewById(R.id.listview_stats).setVisibility(View.VISIBLE);
        adapter.replaceData(courtList);
    }

    @Override
    public void showNoCourt() {
        root.findViewById(R.id.no_stats).setVisibility(View.VISIBLE);
        root.findViewById(R.id.listview_stats).setVisibility(View.GONE);
    }

    @Override
    public void showAddCourt() {
        Intent intent = new Intent(getActivity(), PrepareActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, 100);
    }

    @Override
    public void setPresenter(StatsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onCourtClick((Court)adapter.getItem(i));
    }

    private static class StatsAdapter extends BaseAdapter {
        private List<Court> courts;

        StatsAdapter(List<Court> courts) {
            this.courts = courts;
        }

        public void replaceData(List<Court> courts) {
            this.courts = courts;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return courts.size();
        }

        @Override
        public Object getItem(int i) {
            return courts.get(i);
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
                rowView = inflater.inflate(R.layout.item_stat, viewGroup, false);
            }

            final Court court = (Court) getItem(i);
            ((TextView) rowView.findViewById(R.id.item_stat_score)).setText(
                    Integer.valueOf(court.getScore()).toString() + ":" + court.getScoreCompetitor());

            ((TextView) rowView.findViewById(R.id.item_stat_status)).setText(court.getStatus().name());

            ((TextView) rowView.findViewById(R.id.item_stat_time)).setText(court.getCreatedTime().toString());

            return rowView;
        }
    }


    public void onCourtClick(Court clicked) {
        switch (clicked.getStatus()) {
            case Not_Started:
                Intent intent = new Intent(getActivity(), PrepareActivity.class);
                intent.putExtra(PrepareActivity.KEY_STAT_ID, clicked.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(intent, 1);
                break;
            case Started:
                intent = new Intent(getActivity(), StatActivity.class);
                intent.putExtra(StatActivity.KEY_STAT_ID, clicked.getId());
                startActivityForResult(intent, 1);
                break;
            case Win:
            case Lost:
                intent = new Intent(getActivity(), ReportActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(ReportActivity.KEY_STAT_ID, clicked.getId());
                startActivityForResult(intent, 1);
                break;

        }
    }

}
