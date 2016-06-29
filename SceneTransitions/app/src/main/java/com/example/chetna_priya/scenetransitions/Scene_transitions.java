package com.example.chetna_priya.scenetransitions;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

public class Scene_transitions extends Activity {

    ViewGroup rootContainer;
    Scene scene1;
    Scene scene2;
    Transition transition1, transition2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_transitions);

        transition1 = TransitionInflater.from(this).inflateTransition(R.transition.transition);
        transition2 = TransitionInflater.from(this).inflateTransition(R.transition.transition2);
        rootContainer = (ViewGroup) findViewById(R.id.rootContainer);
        scene1 = Scene.getSceneForLayout(rootContainer, R.layout.scene1_layout, this);
        scene1.enter();

        scene2 = Scene.getSceneForLayout(rootContainer, R.layout.scene2_layout, this);
    }

    public void goToScene1(View view)
    {
        TransitionManager.go(scene1, transition1);
    }

    public void goToScene2(View view)
    {
        TransitionManager.go(scene2, transition1);
    }


}
