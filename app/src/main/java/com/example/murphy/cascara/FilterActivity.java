package com.example.murphy.cascara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.annotation.Nullable;
/** Activity for coffee filters (hehe) based on atmosphere and amenities */
public class FilterActivity extends Activity {
    //Views
    ChipGroup amenities, atmosphere;
    Button filterBtn;

    //Data
    CascaraApplication app;
    String filter;
    String value;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_filters);
        app = (CascaraApplication) getApplication();

        amenities = findViewById(R.id.amenityGroupFilter);
        atmosphere = findViewById(R.id.atmosphereGroupFilter);
        filterBtn = findViewById(R.id.findCoffeeBtn);

        amenities.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                filter = "amenityFilter";
                Chip checked = findViewById(checkedId);
                value = checked.getText().toString();
                Log.e("FILTERING","selected " + value);
            }
        });

        atmosphere.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                filter = "atmosphereFilter";
                Chip checked = findViewById(checkedId);
                value = checked.getText().toString();
                Log.e("FILTERING","selected " + value);
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.getRepo().setFilter(filter);
                app.getRepo().setStringFilterValue(value);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
}
