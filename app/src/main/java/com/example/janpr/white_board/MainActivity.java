package com.example.janpr.white_board;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends Activity implements OnClickListener, OnGesturePerformedListener {

    private DrawingView drawView;
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton drawBtn, eraseBtn, newBtn, equalsBtn;

    GestureLibrary mLibrary;
    String gestureResult="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawingView)findViewById(R.id.drawing);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        equalsBtn = (ImageButton)findViewById(R.id.equals_btn);
        equalsBtn.setOnClickListener(this);

        //on create gestures

        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
            finish();
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestureOverlayView);
        gestures.setGestureStrokeAngleThreshold( 90.0f);//need to figure out side effets of this line

        gestures.addOnGesturePerformedListener(this);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

        ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

        if (predictions.size() > 0 && predictions.get(0).score > 1.1) {
            gestureResult += predictions.get(0).name;

            System.out.println(gestureResult);

        }
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.draw_btn){
            if(!drawView.getErase()) {
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
            }
            drawView.setErase(false);
        }
        else if(view.getId()==R.id.erase_btn){
            if(drawView.getErase()){
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Eraser size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
            }
            drawView.setErase(true);
        }
        else if(view.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New equation");
            newDialog.setMessage("Start new equation?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
            TextView t = (TextView) findViewById(R.id.text_field);
            t.setText("");

        }
        else {
            if (view.getId() == R.id.equals_btn) {
                //save drawing
                System.out.println(gestureResult);
                drawView.startNew();

                if (gestureResult.equals("1") || gestureResult.equals("2") || gestureResult.equals("3") || gestureResult.equals("6")||
                gestureResult.equals("7") || gestureResult.equals("8") || gestureResult.equals("9") || gestureResult.equals("-") ||
                        gestureResult.equals("0")|| gestureResult.matches("[0-9]+"))
                {
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+ gestureResult);
                }
                else if (gestureResult.equals("1-") || gestureResult.equals("-1")) {
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"+");
                }
                else if (gestureResult.equals("4_11") || gestureResult.equals("14_1")) {
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"4");
                }else if (gestureResult.equals("5-") || gestureResult.equals("-5") ||gestureResult.equals("5divide")||gestureResult.equals("divide5")){
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"5");
                }else if (gestureResult.equals("mult1divide") || gestureResult.equals("dividedivide")
                        ||gestureResult.equals("dividemult1") ) {
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"X");
                }else if(gestureResult.equals("divide")){
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"/");
                }else if(gestureResult.equals("exp")){
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"^");
                }else if(gestureResult.equals("rp")){
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+")");
                }else if(gestureResult.equals("lp")){
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"(");
                }else if(gestureResult.equals("minusminus")){
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"=");
                }else if(gestureResult.equals("period")) {
                    TextView t = (TextView) findViewById(R.id.text_field);
                    t.setText(t.getText()+"=");
                }


                gestureResult ="";
            }
        }
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
}
