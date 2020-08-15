package com.stepin.mushroom_recognizer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.stepin.mushroom_recognizer.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class activity_parameter_selection extends AppCompatActivity implements View.OnClickListener {
    public static final int PARAMETERS_COUNT = 9;
    public static final int RESILT_CODE_FOR_CLOSE = 1;
    Button button_next;
    Button button_last;
    ImageView imageView_info;
    TextView textView_descriptions;
    TextView textView_parameters_names;
    Spinner spinner;
    boolean first_click;
    String[][] parameters_types_array;
    String[][] parameters_types_descriptions_array;
    int[][] parameters_types_image_id;
    String[] parameters_descriptions_array;
    String[] parameters_names_array;
    int parameter_number;   // параметр (форма ножки). передается при открытии активити, живет в нем, пока оно существует.
    Intent intent;
    private AdView mAdView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_selection);
        first_click = false;
        parameters_types_array = new String[][]{    // массив массивов для заполнения спинеров
                getResources().getStringArray(R.array.body_shape_types),
                getResources().getStringArray(R.array.mushroom_pulp_types),
                getResources().getStringArray(R.array.hat_surface_types),
                getResources().getStringArray(R.array.hymenophore_type_types),
                getResources().getStringArray(R.array.hat_shape_types),
                getResources().getStringArray(R.array.connection_hat_leg_types),
                getResources().getStringArray(R.array.leg_surface_types),
                getResources().getStringArray(R.array.position_hat_leg_types),
                getResources().getStringArray(R.array.leg_shape_types),
        };

        parameters_types_descriptions_array = new String[][]{
                getResources().getStringArray(R.array.body_shape_types_descriptions),
                getResources().getStringArray(R.array.mushroom_pulp_types_descriptions),
                getResources().getStringArray(R.array.hat_surface_types_descriptions),
                getResources().getStringArray(R.array.hymenophore_type_types_descriptions),
                getResources().getStringArray(R.array.hat_shape_types_descriptions),
                getResources().getStringArray(R.array.connection_hat_leg_types_descriptions),
                getResources().getStringArray(R.array.leg_surface_types_descriptions),
                getResources().getStringArray(R.array.position_hat_leg_types_descriptions),
                getResources().getStringArray(R.array.leg_shape_types_descriptions),
        };

        parameters_types_image_id = new int[][]{
                new int[]{0, R.drawable.shlyapkonozhechnoe, R.drawable.konsolevidnoe, R.drawable.sharovidnoe, R.drawable.chashevidnoe,
                        R.drawable.kopytovidnoe, R.drawable.korallovidnoe, R.drawable.klubnevidnoe, R.drawable.zvyozdchatoe, R.drawable.uhovidnye,
                        R.drawable.rasprostyortoe, R.drawable.grushevidnoe, R.drawable.bulavovidnoe, R.drawable.lopastnye, R.drawable.fallyusovidnoe },
                new int[]{0, R.drawable.myasistaya, R.drawable.kozhistaya, R.drawable.hryashhevidnaya, R.drawable.studenistaya, R.drawable.derevyanistaya,
                        R.drawable.probkovaya, R.drawable.vojlochnayamykot},
                new int[]{0, R.drawable.suhaya, R.drawable.slizistaya, R.drawable.cheshujchatayashlapa, R.drawable.vojlochnayashlapa, R.drawable.voloknistaya,
                        R.drawable.zhelatinoznaya, R.drawable.psevdocheshuychataya},
                new int[]{0, R.drawable.plastinchatyj, R.drawable.trubchatyj, R.drawable.gladkij, R.drawable.shipovatyj, R.drawable.skladchatyj},
                new int[]{0, R.drawable.plokaya, R.drawable.vypuklaya, R.drawable.polusharovidnaya, R.drawable.kolokolchataya, R.drawable.ploskayasuglubleniem,
                        R.drawable.voronkovidnaya, R.drawable.uploshhyonnovypuklayasokruglymbugorkom, R.drawable.nesimmetrichnaya, R.drawable.konicheskaya,
                        R.drawable.vypuklayasvdavlennojseredinoj},
                new int[]{0, R.drawable.silnaya, R.drawable.slabaya},
                new int[]{0, R.drawable.gladkayaisuhaya, R.drawable.cheshujchataya, R.drawable.barhatistaya, R.drawable.setchataya, R.drawable.gladkayaslizistaya,
                        R.drawable.norozdchataya, R.drawable.muarovyjrisunok},
                new int[]{0, R.drawable.tcentralnoe, R.drawable.ekstsentrichnoe, R.drawable.bokovoe},
                new int[]{0, R.drawable.tsilindricheskaya, R.drawable.tolstaya, R.drawable.izgibayushhayasya, R.drawable.massivnaya, R.drawable.dlinnaya,
                        R.drawable.suzhennayaknizu, R.drawable.obratnobulavovidnaya}
        };

        textView_descriptions = findViewById(R.id.textView_desc);
        textView_parameters_names=findViewById(R.id.textView);
        button_next = findViewById(R.id.button_next);
        button_last = findViewById(R.id.button_last);
        imageView_info =findViewById(R.id.imageView_info);
        button_last.setOnClickListener(this);
        button_next.setOnClickListener(this);
        imageView_info.setOnClickListener(this);
        imageView_info.setVisibility(View.INVISIBLE);

        spinner = findViewById(R.id.spinner);
        parameters_descriptions_array = getResources().getStringArray(R.array.parameters_descriptions);
        parameters_names_array = getResources().getStringArray(R.array.parameters_names);
        intent = getIntent();
        parameter_number = intent.getIntExtra("parameter_number", 1);

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> m;
        for(int i = 0; i < parameters_types_array[parameter_number].length; i++)
        {
            m = new HashMap<String, Object>();
            m.put("name", parameters_types_array[parameter_number][i]);
            m.put("picture_id", parameters_types_image_id[parameter_number][i]);
            data.add(m);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.spinner_layout1,new String[]{"name", "picture_id"}, new int[]{R.id.TextView_spinner, R.id.imageView_spinner});
        simpleAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(simpleAdapter);

        textView_descriptions.setText(parameters_descriptions_array[parameter_number]);
        textView_parameters_names.setText(parameters_names_array[parameter_number]);
        try {
            spinner.setSelection(MainActivity.parameters_map.get(parameter_number));
        } catch (NullPointerException e) {spinner.setSelection(0);}
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.parameters_map.put(parameter_number, position);
                if (position!=0){imageView_info.setEnabled(true); imageView_info.setVisibility(View.VISIBLE);}
                else {imageView_info.setEnabled(false); imageView_info.setVisibility(View.INVISIBLE);}
                if (position != 0 && first_click) showDialog(1);
                first_click = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        mAdView = findViewById(R.id.adViewBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_next:
                if (parameter_number < PARAMETERS_COUNT-1) {
                    intent = new Intent(this, activity_parameter_selection.class);
                    intent.putExtra("parameter_number", parameter_number + 1);
                }
                else {intent = new Intent(this, activity_result.class);}
                startActivityForResult(intent, 1);
                break;
            case  R.id.button_last:
                finish();
                break;
           // case R.id.button_info:
               // int i = R.drawable.bulavovidnoe;
               // showDialog(i);
              //  break;
            case R.id.imageView_info:
                showDialog(1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESILT_CODE_FOR_CLOSE)
        {
            Log.d("LOG", "onActivityResult SELECTION" + " " + resultCode);
            setResult(1);
            finish();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", null);
        View view_layout = View.inflate(this,R.layout.info_layout,null);
        ((ImageView)view_layout.findViewById(R.id.imageView)).setImageResource(parameters_types_image_id[parameter_number][MainActivity.parameters_map.get(parameter_number)]);
        builder.setView(view_layout);
        return builder.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        View view_image = dialog.findViewById(R.id.imageView);
        View view_text = dialog.findViewById(R.id.textView_parameters_types_descriptions);
        View view_text_name = dialog.findViewById(R.id.textView_parameters_types_name);
        ((ImageView)view_image).setImageResource(parameters_types_image_id[parameter_number][MainActivity.parameters_map.get(parameter_number)]);
        ((TextView)view_text).setText(parameters_types_descriptions_array[parameter_number][MainActivity.parameters_map.get(parameter_number)]);
        ((TextView)view_text_name).setText(parameters_types_array[parameter_number][MainActivity.parameters_map.get(parameter_number)]);
        dialog.setTitle(parameters_types_array[parameter_number][MainActivity.parameters_map.get(parameter_number)]);
        super.onPrepareDialog(id, dialog, args);
    }
}
