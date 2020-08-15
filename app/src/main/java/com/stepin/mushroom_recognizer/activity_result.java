package com.stepin.mushroom_recognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;

import static android.view.LayoutInflater.from;

public class activity_result extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, descriptions_fragment.call_for_activity {

        RecyclerView recyclerView_result;
        RecyclerViewAdapterResult recyclerViewAdapterResult;
        Button button_back;
        Button button_main;
        Cursor cursor_id;
        int list_position;
        Fragment fragment;
        View fragment_view;
        boolean is_fragment_exist;


static public DataBaseHelper myDbHelper;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        is_fragment_exist = false;
        button_back= findViewById(R.id.button_result_back);
        button_back.setOnClickListener(this);
        button_main=findViewById(R.id.button_result_main);
        button_main.setOnClickListener(this);
        recyclerView_result = findViewById(R.id.recyclerview_result);
        recyclerView_result.setLayoutManager(new LinearLayoutManager(this));

        deleteDatabase("MyDB2");

        myDbHelper = new DataBaseHelper(this);

        try {
        myDbHelper.createDataBase();
        } catch (IOException ioe) {
        }

        try {
        myDbHelper.openDataBase();
        Log.d("LOG","openDataBase");
        } catch (SQLException e) {
        Log.d("LOG","openDataBaseEXCEPTION");
        }
        LoaderManager.getInstance(activity_result.this).initLoader(0, null, activity_result.this);

        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if ((fragment != null) & (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE))
        {
            fragment_view = fragment.getView();
            fragment_view.setVisibility(View.INVISIBLE);
            is_fragment_exist = true;
        }

}

    @NonNull
@Override
public Loader onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("LOGGG", "onCreateLoader");
        return new MyCursorLoader(this);
}

@Override
public void onLoadFinished(@NonNull Loader loader, Cursor data) {
    Log.d("LOGGG", "onLoadFinished");
        if (data.getCount() == 0) ((TextView)findViewById(R.id.textView_result_null)).setText("По вашему запросу ничего не найдено. Попробуйте скорректировать запрос.");
        else ((TextView)findViewById(R.id.textView_result_null)).setText("");
        {
            recyclerViewAdapterResult = new RecyclerViewAdapterResult(this, data);
            recyclerView_result.setAdapter(recyclerViewAdapterResult);
        }
}

@Override
public void onLoaderReset(@NonNull Loader loader) { }

@Override
public void onClick(View v) {
    switch (v.getId())
    {
        case R.id.button_result_back:
        {
            setResult(2);
            finish();
        break;
        }
        case R.id.button_result_main:
        {
            setResult(1);
            finish();
        break;
        }
        }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1: {
                list_position = data.getIntExtra("position", 1);
                recyclerView_result.scrollToPosition(list_position);
                break;
            }
            case 2:
            {
                list_position = data.getIntExtra("position", 1);
                recyclerView_result.scrollToPosition(list_position);
                fragment_view.setVisibility(View.VISIBLE);
                ((descriptions_fragment)fragment).data_from_activity(data.getStringExtra("name"), data.getStringExtra("description"), data.getStringArrayExtra("images_names"));
            }
        }
    }

    @Override
    public void show_dialog(int picture_id) {
        showDialog(picture_id);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", null);
        View view = (View.inflate(this,R.layout.big_image_layout,null));
        ((ImageView)(view.findViewById(R.id.imageView_big))).setImageResource(id);
        builder.setView(view);
        return builder.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        ImageView view = dialog.findViewById(R.id.imageView_big);
        view.setImageResource(id);
        super.onPrepareDialog(id, dialog);
    }


static class MyCursorLoader extends CursorLoader
{
    private Cursor cursor;
    public MyCursorLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        Log.d("LOGGG", "loadInBackground");
        try {
                cursor = myDbHelper.cursorReturn();
        } catch (NullPointerException e) {Log.d("LOG", "NullPointerException in myDbHelper.cursorReturn");}
        return cursor;
    }

}


class RecyclerViewAdapterResult extends RecyclerView.Adapter<RecyclerViewAdapterResult.RecyclerResultHolder>
{
    LayoutInflater inflater;
    Context context;
    Cursor cursor;

    public RecyclerViewAdapterResult(Context context, Cursor cursor)
    {
        this.inflater = from(context);
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterResult.RecyclerResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerResultHolder(inflater.inflate(R.layout.grid_view_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerResultHolder holder, final int position) {
        cursor.moveToPosition(position);
        holder.textView_grid.setText(cursor.getString(cursor.getColumnIndex("name")));
        holder.textView_grid_id.setText(cursor.getString(cursor.getColumnIndex("_id")));
        int image_id = getResources().getIdentifier(cursor.getString(cursor.getColumnIndex("foto1")),"drawable",context.getPackageName());
        holder.imageView_grid.setImageResource(image_id);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t2 = v.findViewById(R.id.textView_grid_id);
                int id1 = Integer.parseInt(t2.getText().toString());
                cursor_id = myDbHelper.cursorReturn_id(id1);
                cursor_id.moveToFirst();
                String image1_name = cursor_id.getString(cursor_id.getColumnIndex("foto1"));
                String image2_name = cursor_id.getString(cursor_id.getColumnIndex("foto2"));
                String image3_name = cursor_id.getString(cursor_id.getColumnIndex("foto3"));
                String name = cursor_id.getString(cursor_id.getColumnIndex("name"));
                String description = cursor_id.getString(cursor_id.getColumnIndex("description"));
                if (!is_fragment_exist) {
                    Intent intent = new Intent(activity_result.this, activity_description.class);
                    intent.putExtra("name", name);
                    intent.putExtra("description", description);
                    intent.putExtra("image_name_1", image1_name);
                    intent.putExtra("image_name_2", image2_name);
                    intent.putExtra("image_name_3", image3_name);
                    intent.putExtra("position", position);
                    cursor_id.close();
                    startActivityForResult(intent, 1);
                } else
                {
                    fragment_view.setVisibility(View.VISIBLE);
                    ((descriptions_fragment)fragment).data_from_activity(name, description, new String[]{image1_name, image2_name, image3_name});

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
    class RecyclerResultHolder extends RecyclerView.ViewHolder
    {
        TextView textView_grid;
        TextView textView_grid_id;
        ImageView imageView_grid;
        public RecyclerResultHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_grid = itemView.findViewById(R.id.textView_grid);
            this.textView_grid_id = itemView.findViewById(R.id.textView_grid_id);
            this.imageView_grid = itemView.findViewById(R.id.imageView_grid);
        }
    }
}
}



