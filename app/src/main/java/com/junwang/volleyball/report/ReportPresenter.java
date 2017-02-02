package com.junwang.volleyball.report;

import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.StatItem;
import com.junwang.volleyball.model.StatResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junwang on 28/01/2017.
 */

public class ReportPresenter implements ReportContract.Presenter {

    ReportContract.View view;
    Court court;

    public ReportPresenter(Court court, ReportContract.View view) {
        this.court = court;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        view.showCreatedTime(court.getCreatedTime().toString());
        view.showScore(court.getScore(), court.getScoreCompetitor());
        view.updateCourtStatus(court.getStatus());
        view.showReport(loadStats());
    }

    @Override
    public void viewDestory() {

    }

    private List<ReportItem> loadStats() {
        final String OTHER_ERROR = "其他失误";

        List<ReportItem> reportItems = new ArrayList<>();
        Map<String, ReportItem> tempMap = new HashMap<>();


        Collection<StatItem> statItems = court.getAllStats();
        for (StatItem statItem : statItems) {
            ReportItem item = null;
            if (statItem.getPlayer() == null) {
                if (tempMap.containsKey(OTHER_ERROR)) {
                    item = tempMap.get(OTHER_ERROR);
                } else {
                    item = new ReportItem(OTHER_ERROR);
                    tempMap.put(OTHER_ERROR, item);
                }

                switch (statItem.getStatResult()) {
                    case ADD_SCORE:
                        item.setWin_score(item.getWin_score()+1);
                        break;
                    case LOOSE_SCORE:
                        item.setLost_score(item.getLost_score()+1);
                        break;
                }
            }


            if (statItem.getPlayer() != null) {
                if (tempMap.containsKey(statItem.getPlayer().getName())) {
                    item = tempMap.get(statItem.getPlayer().getName());
                } else {
                    item = new ReportItem(statItem.getPlayer().getName());
                    tempMap.put(statItem.getPlayer().getName(), item);
                }

                if (statItem.getStatResult().equals(StatResult.ADD_SCORE)) {
                    item.setWin_score(item.getWin_score()+1);
                    switch (statItem.getPlayItem()) {
                        case FAQIU:
                            item.setWin_faqiu(item.getWin_faqiu()+1);
                            break;
                        case JINGONG:
                            item.setWin_jingong(item.getWin_jingong()+1);
                            break;
                        case LANWANG:
                            item.setWin_lanqang(item.getWin_lanqang()+1);
                            break;

                    }
                } else if (statItem.getStatResult().equals(StatResult.LOOSE_SCORE)) {
                    item.setLost_score(item.getLost_score()+1);
                    switch (statItem.getPlayItem()) {
                        case FAQIU:
                            item.setLost_faqiu(item.getLost_faqiu()+1);
                            break;
                        case JINGONG:
                            item.setLost_jingong(item.getLost_jingong()+1);
                            break;
                        case FANGSHOU:
                            item.setLost_fangshou(item.getLost_fangshou()+1);
                            break;
                        case YICHUAN:
                            item.setLost_yichuan(item.getLost_yichuan()+1);
                            break;
                        case LANWANG:
                            item.setLost_lanwang(item.getLost_lanwang()+1);
                            break;
                    }
                }
            }
        }

        for(Map.Entry<String, ReportItem> entry : tempMap.entrySet()) {
            if (entry.getKey().equals(OTHER_ERROR)) {
                continue;
            }
            reportItems.add(entry.getValue());
        }

        ReportItem itemSelfError = tempMap.get(OTHER_ERROR);
        if (itemSelfError!=null) {
            reportItems.add(itemSelfError);
        }

        return  reportItems;

    }
}
