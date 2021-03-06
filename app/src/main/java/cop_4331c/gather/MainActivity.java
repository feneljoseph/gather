package cop_4331c.gather;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import cop_4331c.gather.util.MessageService;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    private TextView Name;
    private TextView userName;
    private TextView phoneNumber;

    private String recipientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));

        }

        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        final ArrayList<String> names = new ArrayList<String>();



        Parse.initialize(this, "r0AWTV2rHQu1LKLuvghS5dxgw32hKeBWDnVmyxNQ", "THis9813mCk50ooetnDlY9wIkAcYDkBn10IE5u2a");

        //don't include yourself
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("objectId", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>()
        {
            public void done(List<ParseUser> userList, com.parse.ParseException e)
            {
                if (e == null) {
                    for (int i=0; i<userList.size(); i++) {
                        names.add(userList.get(i).get("firstName").toString() + " " + userList.get(i).get("lastName").toString());
                        //names.add(userList.get(i).getUsername().toString());
                }
                ListView usersListView = (ListView)findViewById(R.id.sinchUserLayout);
                ArrayAdapter<String> namesArrayAdapter =
                        new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.user_list_item, names);
                usersListView.setAdapter(namesArrayAdapter);
                usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int i, long l)
                    {
                        openConversation(names, i, recipientName);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error loading user list",
                        Toast.LENGTH_LONG).show();
            }
            }
        });

    }

    public void openConversation(final ArrayList<String> names, final int pos, final String s) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("firstName", names.get(pos).split(" ")[0]);
        query.whereEqualTo("lastName", names.get(pos).split(" ")[1]);
        query.findInBackground(new FindCallback<ParseUser>() {

            public void done(List<ParseUser> user, com.parse.ParseException e)
            {
                if (e == null) {
                    Intent openConversation = new Intent(getApplicationContext(), MessagingActivity.class);
                    openConversation.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    openConversation.putExtra("RECIPIENT_NAME", names.get(pos));
                    startActivity(openConversation);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void viewEvent(ArrayList<ParseObject> events, int pos) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(events.get(pos).getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Intent EventList = new Intent (MainActivity.this, new_features_list.class);
                    EventList.putExtra("TargetObjectID", object.getObjectId());
                    startActivity(EventList);

                } else {
                    ProgressDialog dlg = new ProgressDialog(MainActivity.this);
                    dlg.setMessage("Could not get event");
                    dlg.show();
                }
            }
        });
    }


    private int backButtonCount = 0;
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            backButtonCount = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Depending on item selected, perform different action
        switch (item.getItemId()) {
            case R.id.main_search:
                return true;
            case R.id.main_account_info:
                startActivity(new Intent(this, AccountInfoActivity.class));
                return true;
            case R.id.main_sign_out:
                stopService(new Intent(this, MessageService.class));
                ParseUser.getCurrentUser().logOut();
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
                case 0:
                    return new EventFragment();
                case 1:
                    return new ExchangesFragment();
                case 2:
                    return new ProfileFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public class EventFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public EventFragment newInstance(int sectionNumber) {
            EventFragment fragment = new EventFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event, container, false);

            final ArrayList<ParseObject> events = new ArrayList<ParseObject>();
            ParseUser currentUser = ParseUser.getCurrentUser();

            ParseQuery<ParseObject> eventQuery = new ParseQuery("Event");
            eventQuery.whereEqualTo("Attendees", currentUser.getObjectId());
            eventQuery.findInBackground(new FindCallback<ParseObject>()
            {
                @Override
                public void done(List eventList, com.parse.ParseException e)
                {
                    if(e == null) {
                        for (int i=0; i<eventList.size(); i++) {
                            ParseObject temp = (ParseObject) eventList.get(i);
                            events.add(temp);
                        }
                        ListView eventsListView = (ListView)findViewById(R.id.EventLayout);
                        EventListAdapter eventsArrayAdapter =
                                new EventListAdapter(getApplicationContext(),
                                        R.layout.event_list_item, events);
                        eventsListView.setAdapter(eventsArrayAdapter);
                        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> a, View v, int i, long l)
                            {
                                viewEvent(events, i);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error loading event list",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            // Set up the submit button click handler
            rootView.findViewById(R.id.buttonNewEvent).setOnClickListener( new View.OnClickListener() {
                public void onClick(View view) {
                    final Intent EventList = new Intent(MainActivity.this, new_features_list.class);
                    final ParseObject newEvent = new ParseObject("Event");
                    newEvent.put("creator", ParseUser.getCurrentUser());
                    newEvent.add("Attendees", ParseUser.getCurrentUser().getObjectId());

                    try {newEvent.save();}
                    catch (ParseException e) { Toast.makeText(MainActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show(); }

                    final ProgressDialog load = new ProgressDialog(MainActivity.this);
                    load.setTitle("Please wait");
                    load.setMessage("Creating event");
                    load.show();

                    while (newEvent.getObjectId() == null) {}

                    load.dismiss();
                    EventList.putExtra("TargetObjectID", newEvent.getObjectId());
                    startActivity(EventList);

                }
            });

            return rootView;
        }
    }

    public static class ExchangesFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ExchangesFragment newInstance(int sectionNumber) {
            ExchangesFragment fragment = new ExchangesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_exchanges, container, false);

            return rootView;
        }
    }

    public class ProfileFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        private ProfileFragment newInstance(int sectionNumber) {
            ProfileFragment fragment = new ProfileFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

            ParseUser currentUser = ParseUser.getCurrentUser();
            fillTextView(rootView, R.id.textNameProfile, currentUser.get("firstName").toString() + " " + currentUser.get("lastName").toString());
            fillTextView(rootView, R.id.textUsernameProfile, currentUser.getUsername().toString());

            try {fillTextView(rootView, R.id.textPhoneNumberProfile, currentUser.get("phoneNumber").toString());}
            catch (Exception e) {fillTextView(rootView, R.id.textPhoneNumberProfile, "No phone number registered");};

            return rootView;
        }
    }

    public void fillTextView (View layout, int tViewID, String text) {
        TextView temp = (TextView) layout.findViewById(tViewID);
        temp.setText(text);
    }

    private static class EventListAdapter extends ArrayAdapter<ParseObject> {

        private static class ViewHolder {
            private TextView text1;
            private TextView text2;
            private TextView text3;

            ViewHolder() {
            }
        }

        /** Inflater for list items */
        private final LayoutInflater inflater;

        /**
         * General constructor
         *
         * @param context
         * @param resource
         * @param objects
         */
        public EventListAdapter(final Context context,
                                   final int resource,
                                   final List<ParseObject> objects) {
            super(context, resource, objects);

            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            View itemView = convertView;
            ViewHolder holder = null;
            final ParseObject item = getItem(position);
            final ParseUser creator = (ParseUser) item.get("creator");
            final TimeZone tz = TimeZone.getDefault();
            final Date startDate = (Date) item.get("startDate");
            final DateFormat df = new SimpleDateFormat("h:m a, E, MMM d");

            if(null == itemView) {
                itemView = this.inflater.inflate(R.layout.event_list_item, parent, false);

                holder = new ViewHolder();

                holder.text1 = (TextView)itemView.findViewById(R.id.eventList_name);
                holder.text2 = (TextView)itemView.findViewById(R.id.eventList_date);
                holder.text3 = (TextView)itemView.findViewById(R.id.eventList_creator);

                itemView.setTag(holder);
            } else {
                holder = (ViewHolder)itemView.getTag();
            }

            try {holder.text1.setText(item.get("name").toString());}
            catch (Exception e) {holder.text1.setText("No event name set");}

            try {holder.text2.setText(df.format(startDate));}
            catch (Exception e) {holder.text2.setText("No event date set");}

            try {holder.text3.setText(creator.get("firstName").toString() + " " + creator.get("lastName").toString());}
            catch (Exception ex) {}

            return itemView;
        }
    }
}

