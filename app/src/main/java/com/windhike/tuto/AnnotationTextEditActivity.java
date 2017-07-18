package com.windhike.tuto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.windhike.tuto.reuse.ToolbarBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnnotationTextEditActivity extends BaseAty {
    private static final String KEY_EXIST_STR = "key_exist_str";
    @BindView(R.id.et_text)
    EditText etText;
    @BindView(R.id.ll_text)
    LinearLayout llText;

    public static Intent getTextEditIntent(Context context, String existStr) {
        Intent intent = new Intent(context, AnnotationTextEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXIST_STR,existStr);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_text_edit);
        ButterKnife.bind(this);
        getToolbarBuilder().showLeft(true)
                .withLeft("取消")
                .withBack(false)
                .showRight(false)
                .withRight("完成")
                .withRightClick(new ToolbarBuilder.OnClickRight() {
                    @Override
                    public void onClickRight() {
                        String existStr = etText.getText().toString();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(Intent.EXTRA_RETURN_RESULT,existStr);
                        setResult(Activity.RESULT_OK,resultIntent);
                        finish();
                    }
                }).show();
        String existStr = getIntent().getStringExtra(KEY_EXIST_STR);
        if (existStr != null) {
            etText.setText(existStr);
            etText.setSelection(etText.getText().length());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
