package com.stepin.mushroom_recognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.stepin.mushroom_recognizer.R;

public class activity_description extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    ImageView imageView_description1;
    ImageView imageView_description2;
    int active_picture_number;
    TextView textView_description_had;
    TextView textView_description_body;
    int[] images_id_array;
    ImageSwitcher imageSwitcher;
    Button button_back;
    float pos_x_2;
    int list_position;
    Intent intent;
    String image_name_1;
    String image_name_2;
    String image_name_3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_description);

            textView_description_body = findViewById(R.id.textView_description_body);
            textView_description_had = findViewById(R.id.textView_description_had);

            active_picture_number = 0;
            imageSwitcher = findViewById(R.id.imageSwitcher_description);
            imageSwitcher.setOnTouchListener(this);

            imageView_description1 = findViewById(R.id.imageView_description1);
            imageView_description2 = findViewById(R.id.imageView_description2);
            button_back = findViewById(R.id.button_description_back);
            button_back.setOnClickListener(this);
            Intent intent = getIntent();
            list_position = intent.getIntExtra("position", 1);
            textView_description_body.setText(intent.getStringExtra("description"));
            textView_description_had.setText(intent.getStringExtra("name"));
            image_name_1 = intent.getStringExtra("image_name_1");
            image_name_2 = intent.getStringExtra("image_name_2");
            image_name_3 = intent.getStringExtra("image_name_3");
            int id_picture1 = getResources().getIdentifier(image_name_1, "drawable", "com.stepin.mushroom_recognizer");
            imageView_description1.setImageResource(id_picture1);
            int id_picture2 = getResources().getIdentifier(image_name_2, "drawable", "com.stepin.mushroom_recognizer");
            imageView_description2.setImageResource(id_picture2);
            int id_picture3 = getResources().getIdentifier(image_name_3, "drawable", "com.stepin.mushroom_recognizer");

            images_id_array = new int[]{id_picture1, id_picture2, id_picture3};
            Log.d("LOG", intent.getStringExtra("name"));
        }
        catch (OutOfMemoryError e){Log.d("LOG", "OutOfMemoryError");}

        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) & ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_NORMAL))
        {
            intent = new Intent();
            intent.putExtra("position", list_position);
            intent.putExtra("name", textView_description_had.getText().toString());
            intent.putExtra("description", textView_description_body.getText().toString());
            intent.putExtra("images_names", new String[]{image_name_1,image_name_2,image_name_3});
            intent.putExtra("image_name_1", image_name_1);
            intent.putExtra("image_name_2", image_name_2);
            intent.putExtra("image_name_3", image_name_3);
            setResult(2, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        intent = new Intent();
        intent.putExtra("position", list_position);
        setResult(1, intent);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId())
        {
            case R.id.imageSwitcher_description: {
                if (event.getAction() == 0)

                    pos_x_2 = event.getX();

                if (event.getAction() == 2) {

                }
                if (event.getAction() == 1) {
                    if (event.getX() < pos_x_2) {

                        Animation inAnimation = new TranslateAnimation(500,0,1,1);
                        inAnimation.setDuration(300);
                        Animation outAnimation = new TranslateAnimation(0,-500,1,1);
                        outAnimation.setDuration(300);
                        imageSwitcher.setInAnimation(inAnimation);
                        imageSwitcher.setOutAnimation(outAnimation);
                        if (active_picture_number == 2) active_picture_number = 0;
                        else active_picture_number += 1;

                        ((ImageView) imageSwitcher.getNextView()).setImageResource(images_id_array[active_picture_number]);
                        imageSwitcher.showNext();
                        ((TextView)findViewById(R.id.textView_description_number)).setText("Фото " + (active_picture_number + 1) + " из 3");
                    }
                    else if (event.getX() > pos_x_2)
                    {
                        Animation inAnimation = new TranslateAnimation(-500,0,1,1);
                        inAnimation.setDuration(300);
                        Animation outAnimation = new TranslateAnimation(0,500,1,1);
                        outAnimation.setDuration(300);
                        imageSwitcher.setInAnimation(inAnimation);
                        imageSwitcher.setOutAnimation(outAnimation);
                        if (active_picture_number == 0) active_picture_number = 2;
                        else active_picture_number -= 1;

                        ((ImageView) imageSwitcher.getNextView()).setImageResource(images_id_array[active_picture_number]);
                        imageSwitcher.showNext();
                        ((TextView)findViewById(R.id.textView_description_number)).setText("Фото " + (active_picture_number + 1) + " из 3");
                    }
                    else if (event.getX() == pos_x_2) {
                        showDialog(1);
                    }
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", null);
        View view = (View.inflate(this,R.layout.big_image_layout,null));
        ((ImageView)(view.findViewById(R.id.imageView_big))).setImageResource(images_id_array[active_picture_number]);
        builder.setView(view);
        return builder.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
         ImageView view = dialog.findViewById(R.id.imageView_big);
         view.setImageResource(images_id_array[active_picture_number]);
        super.onPrepareDialog(id, dialog);
    }
}
