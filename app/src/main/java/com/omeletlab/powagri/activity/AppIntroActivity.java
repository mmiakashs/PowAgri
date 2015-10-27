package com.omeletlab.powagri.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.omeletlab.powagri.R;
import com.omeletlab.powagri.fragment.AppIntroSlideFragment;
import com.omeletlab.powagri.util.GlobalConstant;

public class AppIntroActivity extends AppIntro {


    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void init(Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        String appIntroStatus = preferences.getString(GlobalConstant.TAG_APP_INTRO, GlobalConstant.TAG_YES);

        if (appIntroStatus.equals(GlobalConstant.TAG_NO)) {
            loadMainActivity();
        }

        addSlide(AppIntroSlideFragment.newInstance(R.layout.app_intro_one));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.app_intro_two));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.app_intro_three));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.app_intro_four));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.app_intro_five));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.app_intro_six));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.app_intro_seven));

    }

    private void loadMainActivity() {
        editor.putString(GlobalConstant.TAG_APP_INTRO, GlobalConstant.TAG_NO);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }
}
