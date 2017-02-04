package com.junwang.volleyball.players;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.junwang.volleyball.R;
import com.junwang.volleyball.model.Player;
import com.junwang.volleyball.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;


public class PlayersFragment extends Fragment implements PlayersContract.View {
    private static final String ARG_FILTER = "filter";
    private static final String ARG_POSITION = "position";

    private PlayersContract.Presenter mPresenter;
    private PlayersAdapter mPlayersAdapter;
    private View root;

    public PlayersFragment() {
        // Required empty public constructor
    }


    public static PlayersFragment newInstance(String filter, String position) {
        PlayersFragment fragment = new PlayersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTER, filter);
        args.putString(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mPlayersAdapter = new PlayersAdapter(new ArrayList<Player>(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.players_frag, container, false);

        SwipeMenuListView listView = (SwipeMenuListView) view.findViewById(R.id.listview_players);
        listView.setAdapter(mPlayersAdapter);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_player);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final EditText input = new EditText(getActivity());
                alert.setView(input);
                alert.setTitle("请输入名称");
                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Player player = new Player(input.getEditableText().toString());
                        mPresenter.addPlayer(player);
                    }
                });
                alert.setNegativeButton("取消", null);
                alert.show();
            }
        });

        //swipe delete
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
                        mPresenter.deletePlayer((Player)mPlayersAdapter.getItem(position));
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        //swipe referesh
        SwipeRefreshLayout layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        layout.setEnabled(true);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPlayers(true);
            }
        });


        root = view;

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.viewDestory();
    }

    @Override
    public void showPlayers(final List<Player> playerList) {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((SwipeRefreshLayout) root.findViewById(R.id.refresh_layout)).setRefreshing(false);
                root.findViewById(R.id.no_player_info).setVisibility(View.GONE);
                root.findViewById(R.id.listview_players).setVisibility(View.VISIBLE);
                mPlayersAdapter.replaceData(playerList);
            }
        });
    }

    @Override
    public void showNoPlayer() {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((SwipeRefreshLayout) root.findViewById(R.id.refresh_layout)).setRefreshing(false);
                root.findViewById(R.id.no_player_info).setVisibility(View.VISIBLE);
                root.findViewById(R.id.listview_players).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setPresenter(PlayersContract.Presenter presenter) {
        mPresenter = presenter;
    }


    private static class PlayersAdapter extends BaseAdapter {
        private List<Player> players;

        PlayersAdapter(List<Player> list) {
            this.players = list;
        }

        public void replaceData(List<Player> players) {
            this.players = players;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int i) {
            return players.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.item_player, null);
                holder.tv_name = (TextView) view.findViewById(R.id.item_player_name);
                view.setTag(R.id.tag_player_viewholder, holder);
            } else {
                holder = (ViewHolder) view.getTag(R.id.tag_player_viewholder);
            }

            final Player item = (Player) getItem(i);

            view.setTag(R.id.tag_player_data, item);

            holder.tv_name.setText(item.getName());

            return view;
        }

        static class ViewHolder {
            TextView tv_name;
        }

        ;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }
}
