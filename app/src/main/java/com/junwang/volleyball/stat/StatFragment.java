package com.junwang.volleyball.stat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.junwang.volleyball.players.PlayersActivity;
import com.junwang.volleyball.R;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.PlayItem;
import com.junwang.volleyball.model.Player;
import com.junwang.volleyball.util.StringResUtil;

import java.util.Set;

/**
 * Created by junwang on 15/01/2017.
 */

public class StatFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    public static final String NAME = "name";
    public static final String FAQIU = "faqiu";
    public static final String SCORE_WIN = "score_win";
    public static final String SCORE_LOST = "score_lost";
    public static final String LISTENER = "listener";

    private Set<PlayItem> availablePositives;
    private Set<PlayItem> availableNegotives;

    StatFragmentListener listener;
    private View root;

    public void setListener(StatFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(com.junwang.volleyball.R.layout.stat_frag, container, false);

        ((Button) view.findViewById(R.id.btn_add_score)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btn_loose_score)).setOnClickListener(this);

        root = view;

        return view;
    }


    public void showStatPad(String name, boolean isFaqiu, int score_win, int score_lost, Set<PlayItem> availablePositives, Set<PlayItem> availableNegotives) {
        View view = root;

        int net = score_win - score_lost;

        view.findViewById(R.id.stat_pad).setVisibility(View.VISIBLE);
        if (name != null) {
            ((TextView) view.findViewById(R.id.name)).setText(isFaqiu ? "*" + name : "" + name);
        }

        if (score_win >= 0) {
            ((TextView) view.findViewById(R.id.score_win)).setText("+" + score_win);
        }
        if (score_lost >= 0) {
            ((TextView) view.findViewById(R.id.score_lost)).setText("-" + score_lost);
        }

        if (score_lost >=0 && score_win >=0) {
            String netStr = (net >=0) ? ("+" + net) : Integer.valueOf(net).toString();
            ((TextView) view.findViewById(R.id.net_score)).setText(netStr);
        }

        if (availableNegotives != null) {
            this.availableNegotives = availableNegotives;
        }

        if (availablePositives != null) {
            this.availablePositives = availablePositives;
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_add_score:
                selectPlayItem(true);
                break;
            case R.id.btn_loose_score:
                selectPlayItem(false);
                break;
        }
    }


    private void selectPlayItem(final boolean positive) {
        Set<PlayItem> setItems = positive ? availablePositives : availableNegotives;

        final int total = setItems.size();

        final PlayItem[] arrayItems = new PlayItem[total];
        setItems.toArray(arrayItems);

        final String[] itemNames = new String[total];
        for (int i = 0; i < total; i++) {
            itemNames[i] = StringResUtil.getString(getActivity(), arrayItems[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(itemNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which >= 0 && which < total) {
                    listener.onStatItemSelected(StatFragment.this, arrayItems[which], positive);
                }
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public interface StatFragmentListener {
        void onStatItemSelected(StatFragment fragment, PlayItem item, boolean positive);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                break;
        }
    }

    public void disableStat() {
        root.findViewById(R.id.btn_add_score).setEnabled(false);
        root.findViewById(R.id.btn_loose_score).setEnabled(false);
    }

    public void enableStat() {
        root.findViewById(R.id.btn_add_score).setEnabled(true);
        root.findViewById(R.id.btn_loose_score).setEnabled(true);
    }
}
