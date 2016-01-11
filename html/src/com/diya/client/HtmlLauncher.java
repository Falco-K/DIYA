package com.diya.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.diya.DiyaMain;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
        		GwtApplicationConfiguration temp = new GwtApplicationConfiguration(600, 400);
        		temp.antialiasing = true;
                return temp;
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new DiyaMain();
        }
}