package com.alibaba.marco;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.marco_debug.MarcoDebugActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       startActivity(new Intent(this, MarcoDebugActivity.class));
       finish();
    }

}