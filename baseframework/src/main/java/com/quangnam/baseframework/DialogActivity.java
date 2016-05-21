package com.quangnam.baseframework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends BaseActivity {

    public static final String ARG_DIALOG_TYPE = "dialog_type";
    public static final String ARG_MESSAGE = "message";

    private DialogType mType;
    private String mMessage;

    private TextView mTvMessage;
    private Button mBtnOk;
    private Button mBtnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        mTvMessage = (TextView) findViewById(R.id.tv_message);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);

        updateDialog(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void updateDialog(Intent intent) {
        DialogType type = (DialogType) intent.getSerializableExtra(ARG_DIALOG_TYPE);
        String message = intent.getStringExtra(ARG_MESSAGE);

        if (mType == null
                || (mType == DialogType.ERROR && type == DialogType.FATAL)) {
            // Only update dialog if a critical message happen
            mType = type;
            mMessage = message;

            mTvMessage.setText(mMessage);

            switch (mType) {
                case ERROR:
                    mBtnCancel.setVisibility(View.GONE);
                    break;
                case FATAL:
                    break;
            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            finish();
        } else if (v.getId() == R.id.btn_cancel) {
            finish();
        }
    }

    public enum DialogType {
        FATAL,
        ERROR
    }
}
