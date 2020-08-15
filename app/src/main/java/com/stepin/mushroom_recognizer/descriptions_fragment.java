package com.stepin.mushroom_recognizer;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class descriptions_fragment extends Fragment implements View.OnTouchListener {
    ImageView imageView_description1;
    ImageView imageView_description2;
    int active_picture_number;
    TextView textView_description_had;
    TextView textView_description_body;
    TextView textView_description_number;
    int[] images_id_array;
    ImageSwitcher imageSwitcher;
    ScrollView scrollView;
    Button button_back;
    float pos_x_1;
    float pos_x_2;
    boolean action;
    boolean big_size;
    int list_position;
    int id_picture1;
    int id_picture2;
    int id_picture3;
    Activity activity;

    private DescriptionsFragmentViewModel mViewModel;

    public static descriptions_fragment newInstance() {
        return new descriptions_fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_description, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DescriptionsFragmentViewModel.class);
        // TODO: Use the ViewModel

        activity = getActivity();
        scrollView = activity.findViewById(R.id.scrollView_description);
        activity.findViewById(R.id.button_description_back).setVisibility(View.GONE);
        active_picture_number = 0;
        imageSwitcher = activity.findViewById(R.id.imageSwitcher_description);
        imageSwitcher.setOnTouchListener(this);

        imageView_description1 = activity.findViewById(R.id.imageView_description1);
        imageView_description2 = activity.findViewById(R.id.imageView_description2);
        textView_description_body = activity.findViewById(R.id.textView_description_body);
        textView_description_had = activity.findViewById(R.id.textView_description_had);
        textView_description_number = activity.findViewById(R.id.textView_description_number);
    }

    public void data_from_activity(String name, String description, String[] images_names_array)
    {
        id_picture1 = getResources().getIdentifier(images_names_array[0], "drawable", "com.stepin.mushroom_recognizer");
        id_picture2 = getResources().getIdentifier(images_names_array[1], "drawable", "com.stepin.mushroom_recognizer");
        id_picture3 = getResources().getIdentifier(images_names_array[2], "drawable", "com.stepin.mushroom_recognizer");
        images_id_array = new int[]{id_picture1, id_picture2, id_picture3};
        active_picture_number = 0;
        textView_description_number.setText("Фото " + (active_picture_number + 1) + " из 3");
        textView_description_body.setText(description);
        textView_description_had.setText(name);
        scrollView.scrollTo(1,1);
        imageSwitcher.showPrevious();
        ((ImageView) imageSwitcher.getCurrentView()).setImageResource(images_id_array[active_picture_number]);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId())
        {
            case R.id.imageSwitcher_description: {
                if (event.getAction() == 0)
                    pos_x_2 = event.getX();
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
                        textView_description_number.setText("Фото " + (active_picture_number + 1) + " из 3");
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
                        textView_description_number.setText("Фото " + (active_picture_number + 1) + " из 3");
                    }
                    else if (event.getX() == pos_x_2) {
                        ((call_for_activity)activity).show_dialog(images_id_array[active_picture_number]);
                    }
                }
                break;
            }
        }
        return true;
    }

    public interface call_for_activity
    {
       void show_dialog(int picture_id);
    }
}
