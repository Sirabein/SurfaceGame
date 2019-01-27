package ru.pavlenty.surfacegame2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private ArrayList<Star> stars = new ArrayList<Star>();

    private Enemy[] enemies = new Enemy[2];

    private Friend friend;

    int screenX;
    int countMisses;

    Bitmap boom;

    private boolean isGameOver;
    private boolean isEnemyCollided;
    private boolean isFriendCollided;
    public boolean hitBoxes;

    int score;
    int multi = 1;
    int time;
    int scoreX;
    int scoreY;
    int sound = 0;

    String lives;

    int highScore[] = new int[5];

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    static MediaPlayer gameOnSound;
    final MediaPlayer gameOverSound;

    Context context;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

        friend = new Friend(context, screenX, screenY);

        enemies[0] = new Enemy(context, screenX, screenY, screenX + 150);
        enemies[1] = new Enemy(context, screenX, screenY, screenX + 700);

        this.screenX = screenX;
        countMisses = 0;
        isGameOver = false;

        score = 0;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);

        highScore[0] = sharedPreferences.getInt("score1", 0);
        highScore[1] = sharedPreferences.getInt("score2", 0);
        highScore[2] = sharedPreferences.getInt("score3", 0);
        highScore[3] = sharedPreferences.getInt("score4", 0);
        this.context = context;

        gameOnSound = MediaPlayer.create(context,R.raw.gameon);
        gameOverSound = MediaPlayer.create(context,R.raw.gameover);

        gameOnSound.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;

        }

        if(isGameOver){
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                Arrays.sort(highScore);
                editor = sharedPreferences.edit();
                editor.putInt("score1", highScore[4]);
                editor.putInt("score2", highScore[3]);
                editor.putInt("score3", highScore[2]);
                editor.putInt("score4", highScore[1]);
                editor.apply();
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.putExtra("hitBoxes",hitBoxes);
                context.startActivity(mainIntent);
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.WHITE);
            paint.setTextSize(20);

            for (Star s : stars) {
                if (isGameOver == false) paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(30);
            canvas.drawText("Scores: " + score + " Multiplier: x" + multi + " Lives: " + lives,100,50,paint);

            if (isFriendCollided) {
                paint.setTextSize(80);
                if (time <= 100) {
                    canvas.drawText("+" + 100 * (multi - 1), scoreX,scoreY - time * 2,paint);
                    time++;
                } else isFriendCollided = false;
            }

            for (Enemy enemy : enemies) {
                canvas.drawBitmap(enemy.getBitmap(), enemy.getX(), enemy.getY(), paint);
            }

            canvas.drawBitmap(friend.getBitmap(), friend.getX(), friend.getY(), paint);

            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            if (hitBoxes) {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(player.getDetectCollisionVertical(), paint);
                canvas.drawRect(player.getDetectCollisionHorizontal(), paint);
                canvas.drawRect(friend.getDetectCollision(), paint);
                for (Enemy enemy : enemies) {
                    canvas.drawRect(enemy.getDetectCollisionVertical(), paint);
                    canvas.drawRect(enemy.getDetectCollisionHorizontal(), paint);
                }
            }

            if (isEnemyCollided) {
                boom = BitmapFactory.decodeResource(context.getResources(), R.drawable.boom);
                canvas.drawBitmap(boom, player.getX() + player.getBitmap().getWidth() / 2 - boom.getWidth() / 2,
                                        player.getY() + player.getBitmap().getHeight() / 2 - boom.getHeight() / 2, paint);
            }

            if (isGameOver){
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setStyle(Paint.Style.FILL);

                int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Game Over",canvas.getWidth() / 2, yPos, paint);
                if (sound < 1) {
                    gameOverSound.start();
                    sound++;
                }
                stopMusic();
                stopAll();
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    public void stopAll() {
        for (Star s : stars) {
            s.setSpeed(0);
        }
        friend.setSpeed(0);
        player.setSpeed(0);
        player.setgravity(0);
        player.stopBoosting();
        for (Enemy enemy : enemies) {
            enemy.setSpeed(0);
        }
    }

    public static void stopMusic(){
        gameOnSound.stop();
    }

    private void update() {

        if (!isGameOver) {
            score += multi;
            highScore[4] = score;
        }

        player.update();

        friend.update(player.getSpeed());

        for (Enemy enemy : enemies) {
            enemy.update(player.getSpeed());
        }

        for (Star s : stars) {
            s.update(player.getSpeed());
        }

        if (Rect.intersects(player.getDetectCollisionHorizontal(), friend.getDetectCollision()) ||
            Rect.intersects(player.getDetectCollisionVertical(), friend.getDetectCollision())) {
            friend.setCollided(true);
            isFriendCollided = true;
            time = 0;
            scoreX = friend.getX() + friend.getBitmap().getWidth() / 2;
            scoreY = friend.getY() + friend.getBitmap().getHeight() / 2;
            score += 100 * multi;
            multi++;
            countMisses = 0;
        } else if (friend.getX() < -friend.getBitmap().getWidth()){
            multi = 1;
            countMisses++;
            friend.setCollided(true);
        }

        if (countMisses == 3) {
            isGameOver = true;
        }

        switch (countMisses){
            case 0: lives = "❤❤❤";
                    break;
            case 1: lives = "❤❤";
                break;
            case 2: lives = "❤";
                break;
            case 3: lives = "";
                break;
        }

        for (Enemy enemy : enemies) {
            if (Rect.intersects(enemy.getDetectCollisionHorizontal(), player.getDetectCollisionHorizontal()) ||
                Rect.intersects(enemy.getDetectCollisionHorizontal(), player.getDetectCollisionVertical()) ||
                Rect.intersects(enemy.getDetectCollisionVertical(), player.getDetectCollisionHorizontal()) ||
                Rect.intersects(enemy.getDetectCollisionVertical(), player.getDetectCollisionVertical())) {
                isEnemyCollided = true;
                countMisses = 3;
            } else if (enemy.getX() < -enemy.getBitmap().getWidth()) {
                enemy.setCollided(true);
            }
        }

    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        gameOnSound.pause();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        gameOnSound.start();
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


}