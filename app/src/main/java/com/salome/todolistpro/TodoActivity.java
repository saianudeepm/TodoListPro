package com.salome.todolistpro;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class TodoActivity extends ActionBarActivity {
    EditText etNewItem;
    ArrayList<String> todoItemsList;
    ArrayAdapter<String> itemListAdapter;
    ListView lvItems;

    private final int EDIT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        readItems();

        lvItems = (ListView) findViewById(R.id.lvItems);
        etNewItem = (EditText) findViewById(R.id.etNewItem);
        itemListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItemsList);
        lvItems.setAdapter(itemListAdapter);

        setupOnListViewListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    // add new items
    public void addNewItem(View view) {

        String addItemValue = etNewItem.getText().toString();
        if(etNewItem.getText().toString().trim().equals("")){
            etNewItem.setText("");
            return;
        }
        etNewItem.setText("");
        itemListAdapter.add(addItemValue);
        saveItems();
        //Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();

    }

    //setting up the listener for deletion of item on long press
    private void setupOnListViewListener(){
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(parent + "" + view + position + id );
                itemListAdapter.remove(todoItemsList.remove(position));
                saveItems();
                return false;
            }
        });

        lvItems.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(TodoActivity.this, EditItemActivity.class);
                intent.putExtra("notePosition", position);
                intent.putExtra("noteText",todoItemsList.get(position));
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });

    }

    //persist the todolist to file system
    private void saveItems(){
        File fileDir = getFilesDir();
        File todoFile =  new File(fileDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, todoItemsList);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // read the todolist on startup
    private void readItems(){
        File fileDir = getFilesDir();
        File todoFile =  new File(fileDir, "todo.txt");
        try{
            todoItemsList = new ArrayList<String>(FileUtils.readLines(todoFile));
        }catch (IOException e){
            todoItemsList = new ArrayList<String>();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            // Extract name value from result extras
            String editNoteText = data.getExtras().getString("editNoteText");
            int position = data.getIntExtra("position", 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            todoItemsList.set(position,editNoteText);
            itemListAdapter.notifyDataSetChanged();
            saveItems();
        }
    }
}
