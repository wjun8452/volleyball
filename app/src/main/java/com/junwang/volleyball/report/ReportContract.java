package com.junwang.volleyball.report;

import com.junwang.volleyball.BasePresenter;
import com.junwang.volleyball.BaseView;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.PlayItem;
import com.junwang.volleyball.model.Player;

import java.util.List;
import java.util.Set;

/**
 * Created by junwang on 28/01/2017.
 */

public interface ReportContract {
    interface View extends BaseView<Presenter> {
        void showReport(List<ReportItem> reportItemList);
        void showCreatedTime(String id);
        void updateCourtStatus(CourtStatus courtStatus);
        void showScore(int mine, int yours);
    }

    interface Presenter extends BasePresenter {

    }
}
