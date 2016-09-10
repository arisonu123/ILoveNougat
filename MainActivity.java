package na.ilovenougat;

import android.app.SearchManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);//make sure toolbar is supported by all versions
        //set toolbar title and icons
        getSupportActionBar().setTitle(R.string.app_title);
        getSupportActionBar().setSubtitle(R.string.app_subtitle);
        getSupportActionBar().setIcon(R.drawable.ic_action_name);

        recyclerView=(RecyclerView) findViewById(R.id.recycler_view_search);
        layoutManager=new LinearLayoutManager(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);//inflate main menu
        //set up searchview search manager
        android.support.v7.widget.SearchView searchView= (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager=(SearchManager)getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }


}
