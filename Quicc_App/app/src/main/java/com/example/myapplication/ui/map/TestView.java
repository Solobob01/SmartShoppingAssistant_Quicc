package com.example.myapplication.ui.map;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.graph.Node;
import com.example.myapplication.ui.shoppingList.ShoppingListFragment;
import com.example.myapplication.ui.shoppingList.ShoppingListViewModel;

import java.util.ArrayList;

public class TestView extends View {
    public static final int LEFT_BORDER_X = 50,RIGHT_BORDER_X = 1050,TOP_BORDER_Y = 50,BOT_BORDER_Y = 1650;
    public static final int VERT_SIZE = BOT_BORDER_Y - TOP_BORDER_Y,HOR_SIZE = RIGHT_BORDER_X - LEFT_BORDER_X;
    public volatile Point location;
    public volatile ArrayList<Node> path = new ArrayList<>();
    boolean running = true;
    private MainActivity mainActivity;

    public class Point{
        public int x,y;
        public Point(int x, int y){
            this.x=x;
            this.y=y;
        }
    }
    Handler viewHandler = new Handler();
    Runnable updateView = new Runnable(){
        @Override
        public void run(){
            findViewById(R.id.draw).invalidate();
            if(running) {
                viewHandler.postDelayed(updateView, 2000);
            }
        }
    };

    Paint defaultPaint, productPaint, shadowPaint, pathPaint, gridPaint, locPaint, nodePaint, textPaint;
    Bitmap testMap;

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        viewHandler.post(updateView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path = ShoppingListFragment.path;

        // Map
        canvas.drawBitmap(testMap, LEFT_BORDER_X, TOP_BORDER_Y, null);

        //Draw points & path
        for (int i = 0; i < path.size() - 2; i++) {
            //System.out.println("X:" + path.get(i).getX() + " Y:" +path.get(i).getY() );
            double p1x = path.get(i).getX(), p1y = path.get(i).getY(), p2x = path.get(i + 1).getX(), p2y = path.get(i + 1).getY();
            canvas.drawLine((float) (LEFT_BORDER_X + p1x), (float) (TOP_BORDER_Y + p1y), (float) (LEFT_BORDER_X + p2x), (float) (TOP_BORDER_Y + p2y), pathPaint);
        }
        canvas.drawCircle(LEFT_BORDER_X + location.x, TOP_BORDER_Y + location.y, 20, locPaint);

        for (int i = 0; i < path.size() - 1; i++) {
            Node n = path.get(i);
            if (n.isProduct()) {
                canvas.drawCircle((float) (LEFT_BORDER_X + n.getX()), (float) (TOP_BORDER_Y + n.getY()), 20, productPaint);
                writeTextAbove((float) (LEFT_BORDER_X + n.getX()), (float) (TOP_BORDER_Y + n.getY()), n.getProduct().getName(), canvas);
            } else {
                canvas.drawCircle((float) (LEFT_BORDER_X + n.getX()), (float) (TOP_BORDER_Y + n.getY()), 10, nodePaint);
            }
        }


        // Map borders
        canvas.drawLine(LEFT_BORDER_X, TOP_BORDER_Y, LEFT_BORDER_X, BOT_BORDER_Y, defaultPaint);
        canvas.drawLine(LEFT_BORDER_X, TOP_BORDER_Y, RIGHT_BORDER_X, TOP_BORDER_Y, defaultPaint);
        canvas.drawLine(RIGHT_BORDER_X, TOP_BORDER_Y, RIGHT_BORDER_X, BOT_BORDER_Y, defaultPaint);
        canvas.drawLine(LEFT_BORDER_X, BOT_BORDER_Y, RIGHT_BORDER_X, BOT_BORDER_Y, defaultPaint);
    }

    private void writeTextAbove(float x, float y, String text, Canvas canvas){
        canvas.drawText(text,x - 10* text.length(),y - 30,textPaint);
    }

    private void init() {

        location = new Point(400,750);

        defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultPaint.setColor(Color.BLACK);
        defaultPaint.setTextSize(100);
        defaultPaint.setStrokeWidth(10);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.RED);
        pathPaint.setStrokeWidth(16);
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodePaint.setColor(Color.RED);
        nodePaint.setStrokeWidth(16);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.GRAY);
        gridPaint.setStrokeWidth(2);

        productPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        productPaint.setStyle(Paint.Style.FILL);
        productPaint.setColor(Color.GREEN);

        locPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        locPaint.setStyle(Paint.Style.FILL);
        locPaint.setColor(Color.argb(200,0,150,255));

        shadowPaint = new Paint(0);
        shadowPaint.setColor(0xff101010);
        shadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.ohdraw);
        testMap = Bitmap.createScaledBitmap(bm,HOR_SIZE,VERT_SIZE,false);
    }
    public void setMainActivity (MainActivity activity){
        this.mainActivity = activity;
    }
}
