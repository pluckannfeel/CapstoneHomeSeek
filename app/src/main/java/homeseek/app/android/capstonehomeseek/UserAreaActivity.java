package homeseek.app.android.capstonehomeseek;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import homeseek.app.android.capstonehomeseek.Fragments.MyAccountFragment;
import homeseek.app.android.capstonehomeseek.Fragments.MyListingsFragment;

public class UserAreaActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    final Activity userAreaActivity = this;
    String email, address, username, firstname, lastname, displayName, cno, userID;
    String image_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        //unfocus on edittexts when starting
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();
        Bundle extra = getIntent().getExtras();
        userID = intent.getStringExtra("userID");
        firstname = intent.getStringExtra("firstname");
        lastname = intent.getStringExtra("lastname");
        email = intent.getStringExtra("email");
        address = intent.getStringExtra("address");
        username = intent.getStringExtra("username");
        cno = intent.getStringExtra("contact_no");
        displayName = firstname + " " + lastname;


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(displayName);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

            Bundle bundle = new Bundle();
            bundle.putString("firstname", firstname);
            bundle.putString("lastname", lastname);
            bundle.putString("username", username);
            bundle.putString("cno", cno);
            bundle.putString("email", email);
            bundle.putString("address", address);
            bundle.putString("userID", userID);
            MyAccountFragment myAccountFragment = new MyAccountFragment();
            MyListingsFragment myListingsFragment = new MyListingsFragment();
            myListingsFragment.setArguments(bundle);
            myAccountFragment.setArguments(bundle);

            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapter.addFragments(myListingsFragment, "My Listings");
            viewPagerAdapter.addFragments(myAccountFragment, "My Account");
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_area_actions,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.log_out:
                showAlertDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    public void showAlertDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //nothing to do
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
        builder.setMessage("Are you sure to Log out?").setNegativeButton("Log out", dialogClickListener)
                .setPositiveButton("Cancel", dialogClickListener).show();


    }
}
