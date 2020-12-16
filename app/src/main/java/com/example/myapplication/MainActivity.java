package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.myapplication.Utils.DatabaseHandler;
import com.example.myapplication.Model.ToDoModel;
import com.example.myapplication.Controller.ToDoController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private DatabaseHandler db;
    public int progressNum = 0;
    public float progressPercent = 0;
    public int progressDone = 0;
    public int dbSize = 0;
    private RecyclerView tasksRecyclerView;
    private ToDoController tasksController;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;
    private ProgressBar progressBar;
    private TextView percentageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).show();

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksController = new ToDoController(db,MainActivity.this);
        tasksRecyclerView.setAdapter(tasksController);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksController));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.fab);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        dbSize = taskList.size();
        if(dbSize != 0) {
            for (int i = 0; i < dbSize; i++) {
                if (taskList.get(i).getStatus() == 1) {
                    progressNum++;
                }
            }
            progressPercent = (float) progressNum / dbSize * 100;
            progressDone = (int) progressPercent;
        }else{
            progressDone = 0;
        }

//        System.out.println("original progress");
//        System.out.println(progressPercent);
//        System.out.println(progressDone);

        String str = progressDone + "%";
        percentageDisplay = findViewById(R.id.percent);
        percentageDisplay.setText(str);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(progressDone);

        tasksController.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        progressNum = 0;
        dbSize = taskList.size();
        if(dbSize != 0) {
            for (int i = 0; i < dbSize; i++) {
                if (taskList.get(i).getStatus() == 1) {
                    progressNum++;
                }
            }
            progressPercent = (float) progressNum / dbSize * 100;
            progressDone = (int) progressPercent;
        }else{
            progressDone = 0;
        }

        System.out.println("new progress");
        System.out.println(progressPercent);
        System.out.println(progressDone);

        tasksController.setTasks(taskList);
        tasksController.notifyDataSetChanged();
        String str = progressDone + "%";
        percentageDisplay = findViewById(R.id.percent);
        percentageDisplay.setText(str);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(progressDone);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        taskList = db.getAllTasks();
        switch (item.getItemId()) {
            case R.id.refresh:
                progressNum = 0;
                dbSize = taskList.size();
                if(dbSize != 0) {
                    for (int i = 0; i < dbSize; i++) {
                        if (taskList.get(i).getStatus() == 1) {
                            progressNum++;
                        }
                    }
                    progressPercent = (float) progressNum / dbSize * 100;
                    progressDone = (int) progressPercent;
                }else{
                    progressDone = 0;
                }

//                System.out.println("new progress");
//                System.out.println(progressPercent);
//                System.out.println(progressDone);

                if(progressDone == 100){
                    Context context = getApplicationContext();
                    CharSequence text = "Congratulations! Keep dueIng it!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();


                }

                String str = progressDone + "%";
                percentageDisplay = findViewById(R.id.percent);
                percentageDisplay.setText(str);
                progressBar = findViewById(R.id.progressBar);
                progressBar.setProgress(progressDone);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}