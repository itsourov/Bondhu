package net.sourov.bondhu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.sourov.bondhu.Adapter.ContactAdapter;
import net.sourov.bondhu.Model.Contacts;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    RecyclerView recyclerView;
    List<Contacts> contactsList;
    ContactAdapter mAdapter;

    FloatingActionButton floatingActionButtonOnFriendList;
    SwipeRefreshLayout refreshLayoutFL;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    TextView nothingFoundTextOnFriendList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mAuth = FirebaseAuth.getInstance();




        nothingFoundTextOnFriendList= findViewById(R.id.nothingFoundTextOnFriendList);
        refreshLayoutFL = findViewById(R.id.refreshLayoutFL);


        //hooks for menu layout
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar_friend_list);
        toolbar.setTitle("Friend list");
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_friend_list);
        navigationView = findViewById(R.id.nav_view_friend_list);
        navigationView.setItemIconTintList(null);

        //navigation toggle
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_deawer_open, R.string.navigation_deawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);








        recyclerView = findViewById(R.id.recyclerViewOnFriendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        displayContacts();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButtonOnFriendList = findViewById(R.id.floatingActionButtonOnFriendList);


        floatingActionButtonOnFriendList.setOnClickListener(v -> {
            startActivity(new Intent(FriendList.this,AddFriends.class));
            finish();
        });

        refreshLayoutFL.setOnRefreshListener(() -> {
          //  displayContacts();
            mAdapter.notifyDataSetChanged();
            refreshLayoutFL.setRefreshing(false);
        });





    }



    private void displayContacts() {
        contactsList =  new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                .child("Friends");
        reference.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                contactsList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Contacts contacts = ds.getValue(Contacts.class);
                    contactsList.add(contacts);
                    mAdapter = new ContactAdapter(FriendList.this,contactsList);
                    recyclerView.setAdapter(mAdapter);
                    refreshLayoutFL.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void processSearch(String query) {
        contactsList =  new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                .child("Friends");
        reference.orderByChild("name").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount()==0){
                    nothingFoundTextOnFriendList.setVisibility(View.VISIBLE);
                    contactsList.clear();
                    mAdapter = new ContactAdapter(FriendList.this,contactsList);
                    recyclerView.setAdapter(mAdapter);
                }else {
                    nothingFoundTextOnFriendList.setVisibility(View.GONE);
                    contactsList.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){

                        Contacts contacts = ds.getValue(Contacts.class);

                        contactsList.add(contacts);
                        mAdapter = new ContactAdapter(FriendList.this,contactsList);
                        recyclerView.setAdapter(mAdapter);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home_menu) {
            startActivity(new Intent(FriendList.this, Dashboard.class));
            finish();

        } else if (itemId == R.id.fd_list_menu) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else if (itemId == R.id.add_fd_menu) {
            startActivity(new Intent(FriendList.this, AddFriends.class));
            finish();
        } else if (itemId == R.id.log_out_menu) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(FriendList.this, LoginActivity.class));
            finish();
        }
        return true;
    }



}