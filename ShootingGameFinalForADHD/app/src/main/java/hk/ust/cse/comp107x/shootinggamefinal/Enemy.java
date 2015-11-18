package hk.ust.cse.comp107x.shootinggamefinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by muppala on 23/5/15.
 */
public class Enemy {
    private final int FIRST_STEPY_VALUE = 5 ;
    private final int NB_SUCCESS_BEFORE_ADD_BALL = 3 ;
    private final int NB_SUCCESS_BEFORE_ACCELERATION = 2 ;
    private final int colors[] = {Color.BLUE , Color.RED , Color.YELLOW , Color.GREEN , Color.GRAY} ;
    Random rd = new Random();

    float x; // Guy's top left corner (x,y)
    float y;
    float radius = 30 ;
    float stepX = 10; // Guy's step in (x,y) direction
    float stepY = FIRST_STEPY_VALUE; // gives speed of motion, larger means faster speed
    int lowerX, lowerY, upperX, upperY; // boundaries
    Paint paint ;

    int enemyShot[] = new int[1] ;
    int level = 0 ;
    int indexEnemyShot = 0 ;


    private Context mContext;

    // Constructors
    public Enemy(Context c) {

        mContext = c;
        paint = new Paint();
        paint.setColor(colors[rd.nextInt(5)]);
    }
    public Enemy(int color, Context c) {

        mContext = c;
        paint = new Paint();
        paint.setColor(color);

    }
    public Enemy(int color, Context c, int x, int radius) {

        mContext = c;
        paint = new Paint();
        paint.setColor(color);
        this.x = x ;
        this.radius = radius ;
    }

    public void beginEnemy(int level ) {
        enemyShot = new int[level/NB_SUCCESS_BEFORE_ADD_BALL + 1] ;
        stepY = FIRST_STEPY_VALUE + level/NB_SUCCESS_BEFORE_ACCELERATION ; ;
    }
    public void setBounds(int lx, int ly, int ux, int uy) {
        lowerX = lx;
        lowerY = ly;
        upperX = ux;
        upperY = uy;

        x = (float) ( radius + (upperX-2*radius)*Math.random());
        y = 0;
    }

    public boolean move() {
        // Get new (x,y) position. Movement is always in vertical direction downwards
        y += stepY;
        // Detect when the guy reaches the bottom of the screen
        // restart at a random location at the top of the screen
        if (y + 50 > upperY) {
            x = (float) ((upperX-50)*Math.random());
            y = 0;
            SoundEffects.INSTANCE.playSound(SoundEffects.SOUND_GUY);
            indexEnemyShot = 0 ;
            return false;
        }
        else
            return true;
    }

    public boolean moveForLine() {
        // Get new (x,y) position. Movement is always in vertical direction downwards
        y += stepY;
        // Detect when the guy reaches the bottom of the screen
        // restart at location at the top of the screen
        if (y + 50 > upperY) {
            SoundEffects.INSTANCE.playSound(SoundEffects.SOUND_GUY);
            return false;
        }
        else
            return true;
    }

    // When you reset, starts the Android Guy from a random X co-ordinate location
    // at the top of the screen again
    public void reset() {
        x = (float) ((upperX-50)*Math.random());
        y = 0;
        paint.setColor(colors[rd.nextInt(5)]);
    }

    public void resetY() {
        y = 0;
    }

    // Returns the rectangle enclosing the Guy. Used for collision detection
    public RectF getRect() {
        return new RectF(x-radius,y-radius,x+radius,y+radius);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getColor() { return paint.getColor();}

    public int[] getEnemyShot() { return enemyShot ;}

    public void draw(Canvas canvas) {

        canvas.drawCircle(x, y, radius, paint);
    }

    public boolean hitAndIfNext() {
        enemyShot[indexEnemyShot] = getColor() ;
        indexEnemyShot ++ ;
        if ( indexEnemyShot == enemyShot.length ) {
            indexEnemyShot = 0 ;
            return true ;
        } else {
            return false ;
        }
    }
}
