package com.junwang.volleyball.prepare;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junwang.volleyball.R;
import com.junwang.volleyball.model.Player;
import com.junwang.volleyball.players.PlayersActivity;

import java.util.List;

/**
 * Created by junwang on 15/01/2017.
 */

public class PrepareFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    public static final String NAME = "name";
    public static final String LISTENER = "listener";
    private Player player;
    private Listener listener;

    public interface Listener {
        List<PlayerInfo> getPlayerCandidates();
        void playerAdded(PrepareFragment fragment, Player player);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prepare_frag, container, false);

        view.findViewById(R.id.btn_add_player).setOnClickListener(this);

        return view;
    }
    
    public void updatePlayer(Player player) {
        this.player = player;

        View view = getView();
        if (player != null) {
            ((TextView) view.findViewById(R.id.name)).setText(player.getName());
        } else {
            ((TextView) view.findViewById(R.id.name)).setText("???");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_add_player:
                selectPlayer();
                break;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void selectPlayer() {
        final List<PlayerInfo> infoList = listener.getPlayerCandidates();
        String[] names = new String[infoList.size()];
        for (int i = 0; i < names.length; i++) {
            PlayerInfo info = infoList.get(i);
            names[i] = info.getPlayer().getName() + (info.isSelected() ? "(已被选)" : "") ;
        }

        if (names.length == 0) {
            openPlayersActivity();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.volleyball);
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                player = infoList.get(which).getPlayer();
                dialog.dismiss();
                updatePlayer(player);
                listener.playerAdded(PrepareFragment.this, player);
            }
        });
        builder.create().show();
    }

    private void openPlayersActivity() {
        Intent intent = new Intent(getActivity(), PlayersActivity.class);
        startActivityForResult(intent, 1);
    }

}
