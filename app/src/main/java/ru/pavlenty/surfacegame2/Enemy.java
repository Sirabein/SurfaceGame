package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class Enemy {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 10;
    private int maxY;
    private int maxX;
    private Rect detectCollisionHorizontal;
    private Rect detectCollisionVertical;
    private boolean isCollided;

    public Enemy(Context context, int screenX, int screenY, int x) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);

        maxX = screenX;
        maxY = screenY - bitmap.getHeight();

        Random generator = new Random();

        this.x = x;
        y = generator.nextInt(maxY);

        speed = generator.nextInt(7) + 5;

        detectCollisionHorizontal =  new Rect(x, y, bitmap.getWidth(), bitmap.getWidth());
        detectCollisionVertical = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update(int playerSpeed) {

        x -= playerSpeed;
        x -= speed;

        if (isCollided) {
            x = maxX + bitmap.getWidth() * 2;
            Random generator = new Random();
            y = generator.nextInt(maxY);
            speed = generator.nextInt(7) + 5;
            isCollided = false;
        }

        detectCollisionHorizontal.left = x;
        detectCollisionHorizontal.top = (int) (y + bitmap.getHeight() * 0.292);
        detectCollisionHorizontal.right = (int) (x + bitmap.getWidth() * 0.79);
        detectCollisionHorizontal.bottom = (int) (y + bitmap.getHeight() * 0.709);

        detectCollisionVertical.left = (int) (x + bitmap.getWidth() * 0.51);
        detectCollisionVertical.top = (int) (y + bitmap.getHeight() * 0.028);
        detectCollisionVertical.right = (int) (x + bitmap.getWidth() * 0.79);
        detectCollisionVertical.bottom = (int) (y + bitmap.getHeight() * 0.98);
    }

    public Rect getDetectCollisionHorizontal() {
        return detectCollisionHorizontal;
    }

    public Rect getDetectCollisionVertical() {
        return detectCollisionVertical;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setCollided(boolean b) {
        this.isCollided = b;
    }
}
