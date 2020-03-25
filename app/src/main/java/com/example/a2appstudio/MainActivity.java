package com.example.a2appstudio;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LinkDialog.LinkDialogListener, ActionMode.Callback {

    private List<Website> lstWebsite;
    private RecyclerViewAdapter adapter;
    private boolean isAscending;

    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    private List<String> links = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isAscending = true;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        RecyclerView rvWebsites = (RecyclerView) findViewById(R.id.weblist_recyclerview);

        lstWebsite = new ArrayList<>();
        applyTexts("https://www.channelnewsasia.com");
        applyTexts("https://google.com");
        applyTexts("https://yahoo.com");

        adapter = new RecyclerViewAdapter(lstWebsite);
        rvWebsites.setAdapter(adapter);
        rvWebsites.setLayoutManager(new LinearLayoutManager(this));

        rvWebsites.addOnItemTouchListener(new RecyclerItemClickListener(this, rvWebsites, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multiSelect(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect){
                    links = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null){
                        actionMode = startActionMode(MainActivity.this); //show ActionMode.
                    }
                }

                multiSelect(position);
            }
        }));
    }



    private void multiSelect(int position) {
        Website website = adapter.getItem(position);
        if (website != null){
            if (actionMode != null) {
                if (links.contains(website.getLink()))
                    links.remove(website.getLink());
                else
                    links.add(website.getLink());

                if (links.size() > 0)
                    actionMode.setTitle(String.valueOf(links.size()));
                else{
                    actionMode.setTitle("");
                    actionMode.finish();
                }
                adapter.setLinks(links);

            }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_delete:
                List<Integer> indexToDelete = new ArrayList<>();
                // iterate through all list of websites
                for (int i = 0; i < lstWebsite.size(); i++) {
                    // if the one in index i in the links
                    if (links.contains(lstWebsite.get(i).getLink())) {
                        // add the index to delete
                        indexToDelete.add(i);
                    }
                }
                deleteList(indexToDelete);
                return true;
        }
        return false;
    }

    public void deleteList(List<Integer> indexToDelete) {
        // sort the index to delete from small to big
        Collections.sort(indexToDelete);
        int i = 0;
        for ( i = 0; i < indexToDelete.size(); i++ ) {
            int deletedIndex = indexToDelete.get(i)-i;
            lstWebsite.remove(deletedIndex);
            adapter.notifyItemRemoved(deletedIndex);
        }
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        isMultiSelect = false;
        links = new ArrayList<>();
        adapter.setLinks(new ArrayList<String>());
    }

    public void openDialog() {
        LinkDialog linkDialog = new LinkDialog();
        linkDialog.show(getSupportFragmentManager(), "link dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sort) {
            if(isAscending) {
                for (int i = 0; i < lstWebsite.size()/2; i++) {
                    moveItem(i, lstWebsite.size()-1-i);
                    moveItem(lstWebsite.size()-2-i, i);
                }
                isAscending = false;
            } else {
                for (int i = 0; i < lstWebsite.size()/2; i++) {
                    moveItem(i, lstWebsite.size()-1-i);
                    moveItem(lstWebsite.size()-2-i, i);
                }
                isAscending = true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void applyTexts(String link) {
        addWebsite(link);
    }

    public void addWebsite(final String link) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String title = "";
                String image = "";
                try {
                    Document document = Jsoup.connect(link).get();
                    title = document.title();
                    Element e1 = document.head().select("link[href~=.*\\.(ico|png)]").first();
                    Element e2 = document.head().select("meta[itemprop=image]").first();
                    if (e1 != null) {
                        image = e1.attr("href");
                    } else if (e2 != null) {
                        image = e2.attr("itemprop");
                    } else {
                        image = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Icon-round-Question_mark.svg/1200px-Icon-round-Question_mark.svg.png";
                    }

                } catch (Exception e) {
                    return;
                }
                final String finalTitle = title;
                final String finalImage = image;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        while(i < lstWebsite.size() && compare(lstWebsite.get(i).getLink(), link) < 0) {
                            i++;
                        }
                        lstWebsite.add(i, new Website(finalTitle, link, finalImage));
                        adapter.notifyItemInserted(i);
                    }
                });
            }
        }).start();
    }

    private int compare(String link, String newlink) {
        if (isAscending) {
            return link.compareTo(newlink);
        } else {
            return (link.compareTo(newlink))*-1;
        }
    }

    private void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition)
            return;

        Website movingItem = lstWebsite.remove(fromPosition);
        if (fromPosition < toPosition) {
            lstWebsite.add(toPosition-1, movingItem);
        } else {
            lstWebsite.add(toPosition, movingItem);
        }

        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    private void sortAscending() {
        Collections.sort(lstWebsite, new Comparator<Website>() {
            @Override
            public int compare(Website o1, Website o2) {
                return o1.getLink().compareTo(o2.getLink());
            }
        });

        adapter.notifyDataSetChanged();
    }

    private void sortDescending() {
        Collections.sort(lstWebsite, new Comparator<Website>() {
            @Override
            public int compare(Website o1, Website o2) {
                return o2.getLink().compareTo(o1.getLink());
            }
        });

        adapter.notifyDataSetChanged();
    }
}
