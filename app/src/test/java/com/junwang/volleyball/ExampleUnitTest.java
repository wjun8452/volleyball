package com.junwang.volleyball;

import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.PlayItem;
import com.junwang.volleyball.model.Player;
import com.junwang.volleyball.model.VolCourt;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void volleyball_add_score() throws Exception {
        Player wangjun = new Player("wangjun");
        Player xiaobo = new Player("xiaobo");
        Player zhanglei = new Player("zhanglei");
        Player jj = new Player("jj");
        Player rongqiu = new Player("rongqiu");
        Player chenxin = new Player("chenxin");

        Court court = new VolCourt();

        assertEquals(CourtStatus.Not_Started, court.getStatus());

        assertTrue(court.addPlayer(1, jj));
        assertTrue(court.addPlayer(2, wangjun));
        assertTrue(court.addPlayer(3, xiaobo));
        assertTrue(court.addPlayer(4, zhanglei));
        assertTrue(court.addPlayer(5, chenxin));

        assertFalse(court.start(true, 4)); //players < 6
        assertEquals(CourtStatus.Not_Started, court.getStatus());

        assertTrue(court.addPlayer(6, rongqiu));

        //prepareCourtDone game
        assertTrue(court.start(true, 4));
        assertFalse(court.addPlayer(2, wangjun));
        assertEquals(0, court.getScore());
        assertEquals(0, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());

        //0:0
        //zhanglei    xiaobo      wangjun
        //chenxin     rongqiu     jj*
        //check player status
        assertTrue(court.isFaqiulun());

        assertEquals(1, court.getPosition(jj));
        assertTrue(court.getPositivePlayItems(jj).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(jj).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(jj).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(jj).contains(PlayItem.JINGONG));
        assertFalse(court.getPositivePlayItems(jj).contains(PlayItem.LANWANG));

        assertTrue(court.getNegotivePlayItems(jj).contains(PlayItem.FAQIU));
        assertFalse(court.getNegotivePlayItems(jj).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(jj).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(jj).contains(PlayItem.JINGONG));
        assertFalse(court.getNegotivePlayItems(jj).contains(PlayItem.LANWANG));

        assertEquals(2, court.getPosition(wangjun));
        assertFalse(court.getPositivePlayItems(wangjun).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(wangjun).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(wangjun).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(wangjun).contains(PlayItem.JINGONG));
        assertTrue(court.getPositivePlayItems(wangjun).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(wangjun).contains(PlayItem.FAQIU));
        assertFalse(court.getNegotivePlayItems(wangjun).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(wangjun).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(wangjun).contains(PlayItem.JINGONG));
        assertTrue(court.getNegotivePlayItems(wangjun).contains(PlayItem.LANWANG));

        assertEquals(3, court.getPosition(xiaobo));
        assertFalse(court.getPositivePlayItems(xiaobo).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(xiaobo).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(xiaobo).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(xiaobo).contains(PlayItem.JINGONG));
        assertTrue(court.getPositivePlayItems(xiaobo).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(xiaobo).contains(PlayItem.FAQIU));
        assertFalse(court.getNegotivePlayItems(xiaobo).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(xiaobo).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(xiaobo).contains(PlayItem.JINGONG));
        assertTrue(court.getNegotivePlayItems(xiaobo).contains(PlayItem.LANWANG));

        assertEquals(4, court.getPosition(zhanglei));
        assertFalse(court.getPositivePlayItems(zhanglei).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(zhanglei).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(zhanglei).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(zhanglei).contains(PlayItem.JINGONG));
        assertTrue(court.getPositivePlayItems(zhanglei).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(zhanglei).contains(PlayItem.FAQIU));
        assertFalse(court.getNegotivePlayItems(zhanglei).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(zhanglei).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(zhanglei).contains(PlayItem.JINGONG));
        assertTrue(court.getNegotivePlayItems(zhanglei).contains(PlayItem.LANWANG));

        assertEquals(5, court.getPosition(chenxin));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(chenxin).contains(PlayItem.JINGONG));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(chenxin).contains(PlayItem.FAQIU));
        assertFalse(court.getNegotivePlayItems(chenxin).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(chenxin).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(chenxin).contains(PlayItem.JINGONG));
        assertFalse(court.getNegotivePlayItems(chenxin).contains(PlayItem.LANWANG));

        assertEquals(6, court.getPosition(rongqiu));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(rongqiu).contains(PlayItem.JINGONG));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(rongqiu).contains(PlayItem.FAQIU));
        assertFalse(court.getNegotivePlayItems(rongqiu).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(rongqiu).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(rongqiu).contains(PlayItem.JINGONG));
        assertFalse(court.getNegotivePlayItems(rongqiu).contains(PlayItem.LANWANG));

        court.addScore(jj, PlayItem.FAQIU);
        //1:0
        //zhanglei    xiaobo      wangjun
        //chenxin     rongqiu     jj*

        assertEquals(1, court.getScore());
        assertEquals(0, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(1, court.getPosition(jj));
        assertEquals(1, court.getPositiveScore(jj));

        court.looseScore(wangjun, PlayItem.LANWANG);
        //1:1
        //zhanglei    xiaobo      wangjun
        //chenxin     rongqiu     jj

        assertEquals(1, court.getScore());
        assertEquals(1, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(1, court.getPosition(jj));
        assertEquals(-1, -court.getNegotiveScore(wangjun)+court.getPositiveScore(wangjun));

        assertFalse(court.isFaqiulun());
        assertEquals(1, court.getPosition(jj));
        assertFalse(court.getPositivePlayItems(jj).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(jj).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(jj).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(jj).contains(PlayItem.JINGONG));
        assertFalse(court.getPositivePlayItems(jj).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(jj).contains(PlayItem.FAQIU));
        assertTrue(court.getNegotivePlayItems(jj).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(jj).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(jj).contains(PlayItem.JINGONG));
        assertFalse(court.getNegotivePlayItems(jj).contains(PlayItem.LANWANG));

        assertEquals(2, court.getPosition(wangjun));
        assertFalse(court.getPositivePlayItems(wangjun).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(wangjun).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(wangjun).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(wangjun).contains(PlayItem.JINGONG));
        assertTrue(court.getPositivePlayItems(wangjun).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(wangjun).contains(PlayItem.FAQIU));
        assertTrue(court.getNegotivePlayItems(wangjun).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(wangjun).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(wangjun).contains(PlayItem.JINGONG));
        assertTrue(court.getNegotivePlayItems(wangjun).contains(PlayItem.LANWANG));

        assertEquals(3, court.getPosition(xiaobo));
        assertFalse(court.getPositivePlayItems(xiaobo).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(xiaobo).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(xiaobo).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(xiaobo).contains(PlayItem.JINGONG));
        assertTrue(court.getPositivePlayItems(xiaobo).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(xiaobo).contains(PlayItem.FAQIU));
        assertTrue(court.getNegotivePlayItems(xiaobo).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(xiaobo).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(xiaobo).contains(PlayItem.JINGONG));
        assertTrue(court.getNegotivePlayItems(xiaobo).contains(PlayItem.LANWANG));

        assertEquals(4, court.getPosition(zhanglei));
        assertFalse(court.getPositivePlayItems(zhanglei).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(zhanglei).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(zhanglei).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(zhanglei).contains(PlayItem.JINGONG));
        assertTrue(court.getPositivePlayItems(zhanglei).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(zhanglei).contains(PlayItem.FAQIU));
        assertTrue(court.getNegotivePlayItems(zhanglei).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(zhanglei).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(zhanglei).contains(PlayItem.JINGONG));
        assertTrue(court.getNegotivePlayItems(zhanglei).contains(PlayItem.LANWANG));

        assertEquals(5, court.getPosition(chenxin));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(chenxin).contains(PlayItem.JINGONG));
        assertFalse(court.getPositivePlayItems(chenxin).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(chenxin).contains(PlayItem.FAQIU));
        assertTrue(court.getNegotivePlayItems(chenxin).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(chenxin).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(chenxin).contains(PlayItem.JINGONG));
        assertFalse(court.getNegotivePlayItems(chenxin).contains(PlayItem.LANWANG));

        assertEquals(6, court.getPosition(rongqiu));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.FAQIU));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.YICHUAN));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.FANGSHOU));
        assertTrue(court.getPositivePlayItems(rongqiu).contains(PlayItem.JINGONG));
        assertFalse(court.getPositivePlayItems(rongqiu).contains(PlayItem.LANWANG));

        assertFalse(court.getNegotivePlayItems(rongqiu).contains(PlayItem.FAQIU));
        assertTrue(court.getNegotivePlayItems(rongqiu).contains(PlayItem.YICHUAN));
        assertTrue(court.getNegotivePlayItems(rongqiu).contains(PlayItem.FANGSHOU));
        assertTrue(court.getNegotivePlayItems(rongqiu).contains(PlayItem.JINGONG));
        assertFalse(court.getNegotivePlayItems(rongqiu).contains(PlayItem.LANWANG));


        court.addScore(zhanglei, PlayItem.JINGONG);
        //2:1
        //chenxin  zhanglei    xiaobo
        //rongqiu   jj     wangjun*

        assertEquals(2, court.getScore());
        assertEquals(1, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(6, court.getPosition(jj));
        assertEquals(1, court.getPositiveScore(zhanglei));

        assertTrue(court.isFaqiulun());

        assertEquals(6, court.getPosition(jj));
        assertEquals(1, court.getPosition(wangjun));
        assertEquals(2, court.getPosition(xiaobo));
        assertEquals(3, court.getPosition(zhanglei));
        assertEquals(4, court.getPosition(chenxin));
        assertEquals(5, court.getPosition(rongqiu));

        court.looseScore();
        //2:2
        //chenxin  zhanglei    xiaobo
        //rongqiu   jj     wangjun

        assertEquals(2, court.getScore());
        assertEquals(2, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(6, court.getPosition(jj));
        assertFalse(court.isFaqiulun());

        court.addScore(rongqiu, PlayItem.JINGONG);
        //3:2
        //rongqiu   chenxin zhanglei
        //jj    wangjun    xiaobo*

        assertEquals(3, court.getScore());
        assertEquals(2, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(5, court.getPosition(jj));
        assertEquals(1, court.getPositiveScore(rongqiu));
        assertTrue(court.isFaqiulun());

        court.looseScore();
        //3:3
        //rongqiu   chenxin zhanglei
        //jj    wangjun    xiaobo

        assertEquals(3, court.getScore());
        assertEquals(3, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(5, court.getPosition(jj));
        assertFalse(court.isFaqiulun());


        court.addScore();
        //4:3
        //jj    rongqiu   chenxin
        //wangjun   xiaobo    zhanglei*

        assertEquals(4, court.getScore());
        assertEquals(3, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(4, court.getPosition(jj));
        assertTrue(court.isFaqiulun());

        court.addScore();
        //5:3
        //jj    rongqiu   chenxin
        //wangjun   xiaobo    zhanglei*

        assertEquals(5, court.getScore());
        assertEquals(3, court.getScoreCompetitor());
        assertEquals(CourtStatus.Win, court.getStatus());
        assertEquals(4, court.getPosition(jj));
        assertTrue(court.isFaqiulun());

        court.undo();
        //4:3
        //jj    rongqiu   chenxin
        //wangjun   xiaobo    zhanglei*
        assertEquals(4, court.getScore());
        assertEquals(3, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(4, court.getPosition(jj));
        assertTrue(court.isFaqiulun());


        court.undo();
        //3:3
        //rongqiu   chenxin zhanglei
        //jj    wangjun    xiaobo
        assertEquals(3, court.getScore());
        assertEquals(3, court.getScoreCompetitor());
        assertEquals(CourtStatus.Started, court.getStatus());
        assertEquals(5, court.getPosition(jj));
        assertFalse(court.isFaqiulun());
    }

}