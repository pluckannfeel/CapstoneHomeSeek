package homeseek.app.android.capstonehomeseek;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import homeseek.app.android.capstonehomeseek.Fragments.HomeFragment;
import homeseek.app.android.capstonehomeseek.Fragments.PinsFragment;
import homeseek.app.android.capstonehomeseek.Fragments.SearchNearbyFragment;
import homeseek.app.android.capstonehomeseek.Fragments.SignInFragment;
import homeseek.app.android.capstonehomeseek.Fragments.TermsConditionsFragment;

public class MainActivity extends AppCompatActivity implements SignInFragment.OnSignInCredentialsPassed{
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Context context;

    // on Sign In fragment (username and password edittexts passed to the main activity)
    @Override
    public void signInCredentialsPass(String uname, String pword) {

    }

    android.support.v4.app.FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
//        findNetwork(viewGroup);

        //unfocus on edittexts when starting
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //retrieve the extra new_user_added from register transaction and this will display toast
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("new_user_added");
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
        }

        //drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        if(savedInstanceState == null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.main_container, new HomeFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Where do you want to live?");
        }


        //navigation initialization
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home_id:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Where do you want to live?");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.sign_in_id:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new SignInFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Sign In");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.pins_id:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new PinsFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Pins");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.search_id:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new SearchNearbyFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Search Nearby");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.rate_id:
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;
                    case R.id.terms_conditions:
                        drawerLayout.closeDrawers();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new TermsConditionsFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Terms and Conditions");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void findNetwork(ViewGroup viewGroup){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            Snackbar snackbar = Snackbar.make(viewGroup, "Internet Connection Detected: " + networkInfo.getTypeName(), Snackbar.LENGTH_LONG);
            snackbar.show();
        } else{
            Snackbar snackbar = Snackbar.make(viewGroup, "No Internet Connection Detected.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

}
