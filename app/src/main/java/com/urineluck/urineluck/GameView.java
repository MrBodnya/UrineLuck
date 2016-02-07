package com.urineluck.urineluck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.transition.Scene;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by abodnya on 2/6/16.
 */
public class GameView extends View {
    Paint paint = new Paint();

    Scene scene;

    public GameView(Context context, AttributeSet attrs) {
        super(context);

        //gameLoop();

        int i = 0;
        int giveUp = 10000;
        while(i<nTarget) {
            tx[i] = (float) (1000*Math.random());
            ty[i] = (float) (1250*Math.random());
            giveUp = giveUp-1;
            int madeIt = 1;
            for(int j=0; j<i; j++) {
                if( (tx[i]-tx[j])*(tx[i]-tx[j]) + (ty[i]-ty[j])*(ty[i]-ty[j]) < targetDist*targetDist) { // too close.

                    madeIt = 0;
                }
            }
            if (madeIt==1 || giveUp<=0) {
                i = i+1;
            }
            else {
                giveUp = giveUp-1;
            }
        }
    }

    int highScore = 0;

    // gravity and mass:
    int nWell = 5;
    float gx[] = new float[nWell];
    float gy[] = new float[nWell];
    float m[] = new float[nWell];

    // Points!
    int nTarget = 15;
    float targetSize = 50;
    float targetDist = 120;
    float tx[] = new float[nTarget];
    float ty[] = new float[nTarget];

    int ix =0;

    public void drawWells(Canvas canvas) {
        paint.setARGB(255, 0, 0, 0);
        for(int i=0; i<nWell; i++) {
            if (m[i]>0) {
                canvas.drawCircle(gx[i],gy[i], 30,paint);
            }
        }
    }

    public void drawStream(Canvas canvas) {
        // simulation time! (also draws targets).

        float x = 1; float y = 1.02f; float vx = 3; float vy = 3; float g = 1000;
        int nStep = 1000;
        int nSubStep = 5;

        int points = 0;
        int tHit[] = new int[nTarget];
        paint.setARGB(255, 180, 180, 180);
        for(int i=0; i<nTarget; i++) {
            canvas.drawCircle(tx[i],ty[i],targetSize,paint);
        }

        for(int i=0; i<nStep; i++) {

            // draw:
            paint.setARGB(255, 120 + (i*7)%120, 120 + i%120, 0);
            canvas.drawCircle(x, y, 5, paint);

            for(int j=0; j<nSubStep; j++) {
                for(int ii=0; ii<nWell; ii++) {
                    float dx = gx[ii]-x;
                    float dy = gy[ii]-y;
                    float d = (float) Math.sqrt(dx*dx + dy*dy);
                    vx = (float) (vx + m[ii]*g*dx/(Math.pow(d,3)));
                    vy = (float) (vy + m[ii]*g*dy/(Math.pow(d,3)));
                }
                x += vx;
                y += vy;

                for(int ii=0; ii<nTarget; ii++) {
                    if((x-tx[ii])*(x-tx[ii]) + (y-ty[ii])*(y-ty[ii]) < targetSize*targetSize) {
                        if(tHit[ii]==0) {
                            tHit[ii] = 1;
                            points = points+1;

                            paint.setARGB(255, 255, 255, 0);
                            canvas.drawCircle(tx[ii],ty[ii],targetSize,paint);
                            paint.setARGB(255, 120, 120, 0);
                        }
                    }
                }
            }
        }

        highScore = Math.max(highScore,points);
        paint.setTextSize(48);
        String sx = "";
        if(m[0] ==0) {
            sx = "Don't stop Peelieving. Add black holes to guide your stream to the many grey bowls.";
        }
        canvas.drawText(sx+" Score: "+points+ " Max at once: "+highScore,10,50,paint);
    }

    public void addWell(float x, float y) {

        if(m[nWell-1]==0) {
            gx[ix] = x;
            gy[ix] = y;
            m[ix] = 1.0f;
            ix = (ix + 1);
        }
        else {
            int best = 0;
            float bestSqrDist = 1000000000000000f;
            for(int i=0; i<nWell; i++) {
                if ( (x-gx[i])*(x-gx[i]) + (y-gy[i])*(y-gy[i]) < bestSqrDist ) {
                    bestSqrDist = (x-gx[i])*(x-gx[i]) + (y-gy[i])*(y-gy[i]);
                    best = i;
                }
            }
            gx[best] = x;
            gy[best] = y;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        //if(action == MotionEvent.ACTION_DOWN) {

        float x = event.getX();
        float y = event.getY();

        for(int i=0; i<nWell; i++) {
            if ( (x-gx[i])*(x-gx[i]) + (y-gy[i])*(y-gy[i]) < 10*10 ) { // too close.
                return true;
            }
        }

            addWell(x, y);

            invalidate();
        //}
        return true;
    }



    // ANIMATION DOES NOT WORK!
    /*
    void gameLoop() {

        while(timeLeft>0) {
            try {
                Thread.sleep(30, 0);
            } catch (InterruptedException e) {
                System.out.println("INTERRUPERD!");
            }
            this.invalidate(); // force a repaint.
            timeLeft = timeLeft-1;
        }
    }
    */

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("DRAW DRAW DRAW!");
        drawStream(canvas);
        drawWells(canvas);
    }
}
