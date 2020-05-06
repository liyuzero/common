package com.yu.lib.common.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*import com.yu.lib.annotations.YuProcessor;
import com.yu.lib.annotations.prossor.BindView;*/

public class MainActivity extends AppCompatActivity {

    //@BindView
    public TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //YuProcessor.bind(this);

        text.setText("呵呵");
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
