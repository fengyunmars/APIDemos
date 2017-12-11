package com.fengyun.russiacell.app;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.dd.plist.PropertyListFormatException;
import com.fengyun.russiacell.view.RussiaGameView;
import com.fengyun.util.ActivityUtils;
import com.fengyun.util.XMLUtils;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

public class GameActivity extends AppCompatActivity {
    RussiaGameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.hideNavigationBar(this);
        gameView = new RussiaGameView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gameView.setLayoutParams(layoutParams);
        setContentView(gameView);
        try {
            XMLUtils.parsePlist("plist/game_render");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (PropertyListFormatException e) {
            e.printStackTrace();
        }
    }

    public void setSurfaceViewDisplay(){
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        int H = mDisplayMetrics.heightPixels;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gameView.getLayoutParams().width = W;
            gameView.getLayoutParams().height = H;

        }else{
            gameView.getLayoutParams().width = W;
            gameView.getLayoutParams().height = H;
        }
    }
}
