
/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tqnam.filemanager.explorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.quangnam.baseframework.BaseDialog;
import com.tqnam.filemanager.R;
import com.tqnam.filemanager.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by quangnam on 2/14/17.
 * Project FileManager-master
 */
public class AboutDialogFragment extends BaseDialog {

    public static final String TAG = AboutDialogFragment.class.getSimpleName();
    public static final String DEVELOPER_EMAIL = "mrquangtn@gmail.com";
    public static final String HOME_PAGE = "https://github.com/namtq1990/FileManager";
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.tv_homepage)
    TextView mTvHomepage;

    private void init() {
        try {
            Context context = getActivity();
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            ViewUtils.formatTitleAndContentTextView(mTvVersion,
                    "Version: ",
                    packageInfo.versionName,
                    null,
                    null);
            ViewUtils.formatTitleAndContentTextView(mTvHomepage,
                    "Home page: ",
                    HOME_PAGE,
                    null,
                    new Object[] {
                            new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    openHomepage();
                                }
                            }
                    });

            mTvHomepage.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View rootView = inflater.inflate(R.layout.dialog_about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog)
                .setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNeutralButton("Feedback", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        feedback();
                    }
                });
        Dialog dialog = builder.create();
        ButterKnife.bind(this, rootView);
        init();

        return dialog;
    }

    public void feedback() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{DEVELOPER_EMAIL});

//        i.setData(Uri.parse("mailto:" + DEVELOPER_EMAIL));
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback about " + getAppContext().getPackageName());
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivitySafe(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openHomepage() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(HOME_PAGE));
        startActivity(i);
    }
//    @BindView(R.id.tv_author)
//    TextView mTvAuthor;
}
