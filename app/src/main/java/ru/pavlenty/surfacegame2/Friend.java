package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class Friend {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 10;
    private int maxY;
    private int maxX;
    private Rect detectCollision;
    private boolean isCollided;

    public Friend(Context context, int screenX, int screenY) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.friend);

        maxX = screenX;
        maxY = screenY - bitmap.getHeight();

        Random generator = new Random();

        x = maxX;
        y = generator.nextInt(maxY);

        speed = generator.nextInt(7) + 5;

        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
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

        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();
    }

    public Rect getDetectCollision() {
        return detectCollision;
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
