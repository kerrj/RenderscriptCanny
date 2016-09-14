package justin.opencvcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;

/**
 * Created by Justin on 6/26/2016.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    public boolean surfaceCreated=false;
    private MyThread thread;
    private String fps="0";
    private long lastFrameMillis;
    private long[] fpsHistory=new long[10];
    private int index=0;
    private float x=0;
    private float y=0;
    private float r=0;

    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
    }
    public void update(int nx, int ny, int nr){
        x=(float)nx;
        y=(float)ny;
        r=(float)nr;
        long elapsed=System.currentTimeMillis()-lastFrameMillis;
        long f=1000/elapsed;
        fpsHistory[index]=f;
        index++;
        if(index>9){
            index=0;
        }
        f=0;
        for(long l:fpsHistory){
            f+=l;
        }
        lastFrameMillis=System.currentTimeMillis();
        fps=Long.toString(f / fpsHistory.length);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceCreated=true;
        thread=new MyThread(getHolder(),this);
        thread.start();
        lastFrameMillis=System.currentTimeMillis();
        for(int i=0;i<10;i++){
            fpsHistory[i]=0;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread.setStop();
    }

    public class MyThread extends Thread{
        private boolean running=true;
        private SurfaceHolder holder;
        private MySurfaceView surfaceView;
        public Canvas mCanvas;

        public MyThread(SurfaceHolder surfaceHolder,MySurfaceView mySurfaceView){
            holder=surfaceHolder;
            surfaceView=mySurfaceView;
        }

        public void setStop(){
            running=false;
        }
        @Override
        public void run(){
            while(running){
                    try {
                        mCanvas = holder.lockCanvas();
                        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        Paint paint=new Paint();
                        paint.setTextSize(40f);
                        paint.setColor(Color.BLUE);
                        mCanvas.drawText(fps,20,40,paint);
                        mCanvas.drawCircle(y + 13, x, r, paint);
//                        surfaceView.draw(mCanvas);
                        holder.unlockCanvasAndPost(mCanvas);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
