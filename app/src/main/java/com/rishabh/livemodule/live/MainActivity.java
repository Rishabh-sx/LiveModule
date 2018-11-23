package com.rishabh.livemodule.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import com.rishabh.livemodule.R;
import com.rishabh.livemodule.live.live.liveCamera.LiveCameraActivity;
import com.rishabh.livemodule.utils.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.btn_go_live)
    Button btnGoLive;
    @BindView(R.id.stream_id)
    TextInputEditText streamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_go_live)
    public void onViewClicked() {
        if (validate()) {
            startActivity(new Intent(this, LiveCameraActivity.class)
                    .putExtra(AppConstants.NAME, name.getText().toString())
                    .putExtra(AppConstants.EMAIL, email.getText().toString())
                    .putExtra(AppConstants.STREAMID,streamId.getText().toString())
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    private boolean validate() {

        if (TextUtils.isEmpty(email.getText())) {
            email.requestFocus();
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.requestFocus();
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(name.getText())) {
            name.setText("");
            name.requestFocus();
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(streamId.getText())) {
            name.setText("");
            name.requestFocus();
            Toast.makeText(this, "Please enter Stream Id", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
