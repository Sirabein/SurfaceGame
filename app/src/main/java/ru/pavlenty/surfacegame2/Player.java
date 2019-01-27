package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Player {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 0;
    private boolean boosting;
    private int gravity = -10;
    private int maxY;
    private int minY;

    private final int MIN_SPEED = 0;
    private final int MAX_SPEED = 20;

    private Rect detectCollisionHorizontal;
    private Rect detectCollisionVertical;

    public Player(Context context, int screenX, int screenY) {
        x = 75;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        maxY = screenY - bitmap.getHeight();
        minY = 0;
        boosting = false;

        detectCollisionHorizontal =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        detectCollisionVertical =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }

    public void update() {
        if (boosting) {
            speed += 2;
        } else {
            speed -= 5;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        y -= speed * 1.5 + gravity;

        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }


        detectCollisionHorizontal.left = (int) (x + bitmap.getWidth() * 0.211);
        detectCollisionHorizontal.top = (int) (y + bitmap.getHeight() * 0.305);
        detectCollisionHorizontal.right = x + bitmap.getWidth();
        detectCollisionHorizontal.bottom = (int) (y + bitmap.getHeight() - Math.floor(bitmap.getHeight() * 0.305));

        detectCollisionVertical.left = detectCollisionHorizontal.left;
        detectCollisionVertical.top = (int) (y + bitmap.getHeight() * 0.033);
        detectCollisionVertical.right = (int) (x + bitmap.getWidth() * 0.493);
        detectCollisionVertical.bottom = (int) (y + bitmap.getHeight() * 0.968);
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setgravity(int gravity) {
        this.gravity = gravity;
    }
}