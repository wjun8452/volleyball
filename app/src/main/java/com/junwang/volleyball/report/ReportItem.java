package com.junwang.volleyball.report;

/**
 * Created by junwang on 01/02/2017.
 */

public class ReportItem {
    private String player;
    private int win_score;
    private int lost_score;
    private int win_jingong;
    private int win_faqiu;
    private int win_lanqang;
    private int lost_jingong;
    private int lost_faqiu;
    private int lost_lanwang;
    private int lost_fangshou;
    private int lost_yichuan;

    public ReportItem(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    public int getWin_score() {
        return win_score;
    }

    public void setWin_score(int win_score) {
        this.win_score = win_score;
    }

    public int getLost_score() {
        return lost_score;
    }

    public void setLost_score(int lost_score) {
        this.lost_score = lost_score;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getWin_jingong() {
        return win_jingong;
    }

    public void setWin_jingong(int win_jingong) {
        this.win_jingong = win_jingong;
    }

    public int getWin_faqiu() {
        return win_faqiu;
    }

    public void setWin_faqiu(int win_faqiu) {
        this.win_faqiu = win_faqiu;
    }

    public int getWin_lanqang() {
        return win_lanqang;
    }

    public void setWin_lanqang(int win_lanqang) {
        this.win_lanqang = win_lanqang;
    }

    public int getLost_jingong() {
        return lost_jingong;
    }

    public void setLost_jingong(int lost_jingong) {
        this.lost_jingong = lost_jingong;
    }

    public int getLost_faqiu() {
        return lost_faqiu;
    }

    public void setLost_faqiu(int lost_faqiu) {
        this.lost_faqiu = lost_faqiu;
    }

    public int getLost_lanwang() {
        return lost_lanwang;
    }

    public void setLost_lanwang(int lost_lanwang) {
        this.lost_lanwang = lost_lanwang;
    }

    public int getLost_fangshou() {
        return lost_fangshou;
    }

    public void setLost_fangshou(int lost_fangshou) {
        this.lost_fangshou = lost_fangshou;
    }

    public int getLost_yichuan() {
        return lost_yichuan;
    }

    public void setLost_yichuan(int lost_yichuan) {
        this.lost_yichuan = lost_yichuan;
    }
}
