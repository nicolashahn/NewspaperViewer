package com.nicolashahn.newspaperviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    static final public String SFG_URL = "http://m.sfgate.com";
    static final public String SJMN_URL = "http://www.mercurynews.com";
    static final public String SCS_URL = "http://www.santacruzsentinel.com";

    static final public String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // clickButton usable for every newspaper
    public void clickButton(View v){
        Button b = (Button) v;
        String url = null;
        switch(b.getId()){
            case R.id.sfgButton:
                url = SFG_URL;
                break;
            case R.id.sjmnButton:
                url = SJMN_URL;
                break;
            case R.id.scsButton:
                url = SCS_URL;
                break;
        }
        Log.i(LOG_TAG, url);

        Intent i = new Intent(MainActivity.this, WebviewActivity1.class);
        i.putExtra("url",url);
        MainActivity.this.startActivity(i);
    }
}
