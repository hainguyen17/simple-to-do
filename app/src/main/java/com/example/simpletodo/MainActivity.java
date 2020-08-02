package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> todos;
    EditText todoInput;
    Button saveBtn;
    RecyclerView todoListView;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoInput = findViewById(R.id.todoInput);
        saveBtn = findViewById(R.id.saveBtn);
        todoListView = findViewById(R.id.todoListView);

        loadItems();

        ItemAdapter.OnClickListener clickListener = new ItemAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity", "Single click at pos " + position);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(KEY_ITEM_TEXT, todos.get(position));
                intent.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(intent, EDIT_TEXT_CODE);
            }
        };
        ItemAdapter.OnLongClickListener longClickListener = new ItemAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                todos.remove(position);
                itemAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            };
        };

        itemAdapter = new ItemAdapter(todos, clickListener, longClickListener);
        todoListView.setAdapter(itemAdapter);
        todoListView.setLayoutManager(new LinearLayoutManager(this));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = todoInput.getText().toString();
                todos.add(todoItem);
                todoInput.setText("");
                itemAdapter.notifyItemInserted(todos.size() - 1);
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            todos.set(position, itemText);
            itemAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item successfully edited", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    private void loadItems() {
        try {
            todos = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            todos = new ArrayList<>();
        }
    }

    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), todos);
        } catch (IOException e) {
            Log.e("MainActivity", "Error saving items", e);
        }
    }
}