package com.junwang.volleyball.players;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.junwang.volleyball.R;
import com.junwang.volleyball.stats.StatsActivity;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

public class PlayersActivity extends AppCompatActivity {
    public static final String KEY_COURT_ID = "id";
    PlayersPresenter presenter;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        PlayersFragment playersFragment =
                (PlayersFragment) getSupportFragmentManager().findFragmentById(R.id.frag_players);
        if (playersFragment == null) {
            // Create the fragment
            playersFragment = new PlayersFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frag_players, playersFragment).commit();
        }

        presenter = new PlayersPresenter(this, playersFragment);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.players_navigation_menu_item:
                                // Do nothing, we're already on that screen
                                break;
                            case R.id.stats_navigation_menu_item:
                                Intent intent =
                                        new Intent(PlayersActivity.this, StatsActivity.class);
                                intent.setFlags(FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
