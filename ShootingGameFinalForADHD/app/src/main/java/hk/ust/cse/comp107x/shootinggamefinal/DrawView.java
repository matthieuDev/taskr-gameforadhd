package hk.ust.cse.comp107x.shootinggamefinal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by muppala on 23/5/15.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    public final static int REMEMBER_STATE = 0 ;
    public final static int SHOOT_STATE = 1 ; // 2 state


    int state ; // current state
    int tostate ;
    int level = 0 ;
    private int width, height;
    private DrawViewThread drawviewthread;

    Context mContext;

    // We can have multiple bullets and explosions
    // keep track of them in ArrayList
    ArrayList<Bullet> bullets;
    ArrayList<Explosion> explosions;
    Cannon cannon;
    //AndroidGuy androidGuy;
    Enemy enemy;
    EnemyLine enemyLine;
    Score score;


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        getHolder().addCallback(this);

        setFocusable(true);
        this.requestFocus();

        // create a cannon object
        cannon = new Cannon(Color.BLUE,mContext);

        // create arraylists to keep track of bullets and explosions
        bullets = new ArrayList<Bullet> ();
        explosions = new ArrayList<Explosion>();

        // create the falling Android Guy
        //androidGuy = new AndroidGuy(Color.RED, mContext);
        enemy = new Enemy( mContext);
        enemy.beginEnemy(0);
        int col[] = {Color.BLUE , Color.GREEN} ;
        enemyLine = new EnemyLine(width , mContext , col) ;
        score = new Score(Color.BLACK);
        state = REMEMBER_STATE ;
        tostate = state ;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        drawviewthread = new DrawViewThread(holder);
        drawviewthread.setRunning(true);
        drawviewthread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        drawviewthread.setRunning(false);

        while (retry){
            try {
                drawviewthread.join();
                retry = false;
            }
            catch (InterruptedException e){

            }
        }

    }

    public class DrawViewThread extends Thread{
        private SurfaceHolder surfaceHolder;
        private boolean threadIsRunning = true;

        public DrawViewThread(SurfaceHolder holder){
            surfaceHolder = holder;
            setName("DrawViewThread");
        }

        public void setRunning (boolean running){
            threadIsRunning = running;
        }

        public void run() {
            Canvas canvas = null;

            while (threadIsRunning) {

                try {
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized(surfaceHolder){
                        drawGameBoard(canvas);
                    }
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        cannon.setBounds(0,0,width, height);
        enemy.setBounds(0,0,width,height);
        enemyLine.setBounds(0, 0, width, height);
        for (int i = 0; i < bullets.size(); i++ ) {
            bullets.get(i).setBounds(0,0,width,height);
        }

    }

    public void drawGameBoard(Canvas canvas) {
        canvas.drawColor(Color.WHITE);     //if you want another background color
        // Draw the cannon
        cannon.draw(canvas);

        // Draw all the bullets
        for (int i = 0; i < bullets.size(); i++) {
            if (bullets.get(i) != null) {
                bullets.get(i).draw(canvas);

                if (bullets.get(i).move() == false) {
                    bullets.remove(i);
                }
            }
        }

        // Draw all the explosions, at those locations where the bullet
        // hits the Android Guy
        for (int i = 0; i < explosions.size(); i++) {
            if (explosions.get(i) != null) {
                if (explosions.get(i).draw(canvas) == false) {
                    explosions.remove(i);
                }
            }
        }

        if ( state == REMEMBER_STATE) {
            // If the Android Guy is falling, check to see if any of the bullets
            // hit the Guy
            if (enemy != null) {
                enemy.draw(canvas);

                RectF guyRect = enemy.getRect();

                for (int i = 0; i < bullets.size(); i++) {

                    // The rectangle surrounding the Guy and Bullet intersect, then it's a collision
                    // Generate an explosion at that location and delete the Guy and bullet. Generate
                    // a new Android Guy to fall from the top.
                    if (RectF.intersects(guyRect, bullets.get(i).getRect())) {
                        explosions.add(new Explosion(Color.RED, mContext, enemy.getX(), enemy.getY()));
                        if ( enemy.hitAndIfNext()) {
                            tostate = SHOOT_STATE ;
                            enemyLine.setColorToShoot(enemy.getEnemyShot());
                        }
                        enemy.reset();
                        bullets.remove(i);
                        // Play the explosion sound by calling the SoundEffects class
                        SoundEffects.INSTANCE.playSound(SoundEffects.SOUND_EXPLOSION);


                       // score.incrementScore();
                        break;
                    }

                }

                if (enemy.move() == false) {
                    levelDecremente();
                    score.decrementScore();

                }

            }
        } else if ( state == SHOOT_STATE) {
            if (enemyLine != null) {
                enemyLine.draw(canvas);
                Enemy line[] = enemyLine.getEnemyLine() ;
                for (int n = 0; n < 5 ; n++) {
                    Enemy androidGuy=line[n] ;
                    RectF guyRect = androidGuy.getRect();

                    for (int i = 0; i < bullets.size(); i++ ) {

                        // The rectangle surrounding the Guy and Bullet intersect, then it's a collision
                        // Generate an explosion at that location and delete the Guy and bullet. Generate
                        // a new Android Guy to fall from the top.
                        if (RectF.intersects(guyRect, bullets.get(i).getRect())) {
                            explosions.add(new Explosion(Color.RED, mContext, androidGuy.getX(), androidGuy.getY()));
                            enemyLine.reset();
                            bullets.remove(i);
                            // Play the explosion sound by calling the SoundEffects class
                            SoundEffects.INSTANCE.playSound(SoundEffects.SOUND_EXPLOSION);
                            int goodHit = enemyLine.gooodShot(androidGuy.getColor()) ;
                            if ( goodHit != 0 ) {
                                score.addScore(goodHit);
                                if (goodHit == 1 ) {
                                    level ++ ;
                                    enemy.beginEnemy(level);
                                } else {
                                    levelDecremente();
                                }

                                tostate = REMEMBER_STATE ;
                            }
                            break;
                        }

                    }
                }


                if (enemyLine.move() == false) {
                    score.decrementScore();
                    levelDecremente();
                    tostate = REMEMBER_STATE ;
                }
            }
        }
        state= tostate ;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawText("Level: " + level, 10, 50, paint);
        score.draw(canvas);

    }

    // Move the cannon left or right
    public void moveCannonLeft() {
        cannon.moveLeft();
    }

    public void moveCannonRight() {
        cannon.moveRight();
    }

    // Whenever the user shoots a bullet, create a new bullet moving upwards
    public void shootCannon() {

        bullets.add(new Bullet(Color.RED, mContext, cannon.getPosition(), (float) (height - 40)));

    }

    public void stopGame(){
        if (drawviewthread != null){
            drawviewthread.setRunning(false);
        }
    }

    public void resumeGame(){
        if (drawviewthread != null){
            drawviewthread.setRunning(true);
        }
    }

    public void releaseResources(){

    }
    public void levelDecremente () {int x = level - 1 ;
        level = x > 0 ? x : 0 ;
        enemy.beginEnemy(level);
    }
}
