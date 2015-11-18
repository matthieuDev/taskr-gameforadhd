package hk.ust.cse.comp107x.shootinggamefinal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by Devaux on 18/11/15.
 */
public class EnemyLine {
    private final int colors[] = {Color.BLUE , Color.RED , Color.YELLOW , Color.GREEN , Color.GRAY} ;
    Enemy enemyLine[] ;

    int colorToShoot[] ;
    int indexToShoot ;
    public EnemyLine(int width, Context mcontext, int[] toShoot) {
        enemyLine = new Enemy[5] ;
        int tenthWidth = width / 10 ;
        for (int i = 0; i < 5; i++) {
            enemyLine[i] = new Enemy(colors[i] , mcontext , (1+i*2)*5 , 30);
        }
        colorToShoot = toShoot ;
        indexToShoot = 0 ;
    }

    public void setBounds(int lx, int ly, int ux, int uy) {
        for (int i = 0; i < 5; i++) {
            enemyLine[i].setBounds(lx , ly , ux , uy );
        }
    }

    public boolean move() {
        boolean res = true ;
        for (int i = 0; i < 5 ; i++) {
            boolean acc = enemyLine[i].moveForLine() ;
            res = res && acc ;
        }
        if ( !res ) {
            indexToShoot = 0 ;
            this.reset();
        }
        return res ;
    }

    public void reset() {
        for (int i = 0; i < 5 ; i++) {
            enemyLine[i].resetY();
        }
    }

    public Enemy[] getEnemyLine() {return enemyLine;}

    public void draw(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            enemyLine[i].draw(canvas);
        }
    }

    public void setColorToShoot(int[] colors) {
        colorToShoot = colors ;
    }

    //when shoot wrong color return -1 when shot good color, if end of the serie of balls return 1
    //if have to continue return 0
    public int gooodShot(int color) {
        if(color == colorToShoot[indexToShoot]) {
            indexToShoot++ ;
            if ( indexToShoot == colorToShoot.length) {
                indexToShoot = 0 ;
                return 1 ;
            } else {
                return 0 ;
            }
        } else {
            indexToShoot = 0 ;
            return -1 ;
        }
    }

}
