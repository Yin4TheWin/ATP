package com.nnmg.atp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    //Variables for constructor
    public Rectangle rectangle;
    private int width;
    private int height;
    private Texture sprite;

    //Variables for animation
    private Texture walkSheet;
    private static final int FRAME_COLS=8, FRAME_ROWS=4;
    public Animation<TextureRegion> downAnim;
    public Animation<TextureRegion> upAnim;
    public Animation<TextureRegion> leftAnim;
    public Animation<TextureRegion> rightAnim;
    private Texture stayStill;
    private float stateTime;

    //Constructor
    public Player(Rectangle rectangle, int width, int height, Texture sprite){
        startAnimate();
        this.rectangle=rectangle;
        this.width=width;
        this.height=height;
        this.sprite=sprite;
    }

    //Animate Method
    public void startAnimate(){
        //Load up all necessary sprites
        walkSheet=new Texture(Gdx.files.internal("walk.png"));
        stayStill=new Texture(Gdx.files.internal("static.png"));
        //Spritesheet split up into 8x4 (columns by rows)
        TextureRegion[][] walkRegion=TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);
        //Further split up by rows into up, down, left, and right
        TextureRegion[] walkDown=new TextureRegion[FRAME_COLS];
        TextureRegion[] walkUp=new TextureRegion[FRAME_COLS];
        TextureRegion[] walkLeft=new TextureRegion[FRAME_COLS];
        TextureRegion[] walkRight=new TextureRegion[FRAME_COLS];
        //Add each individual sprite from each row into an array for animating (Don't worry about this)
        int indexD=0;
        int indexU=0;
        int indexR=0;
        int indexL=0;
        for(int i=0; i<1; i++){
            for(int j=0; j<FRAME_COLS; j++){
                walkDown[indexD++]=walkRegion[i][j];
            }
        }
        for(int i=1; i<2; i++){
            for(int j=0; j<FRAME_COLS; j++){
                walkUp[indexU++]=walkRegion[i][j];
            }
        }
        for(int i=2; i<3; i++){
            for(int j=0; j<FRAME_COLS; j++){
                walkLeft[indexL++]=walkRegion[i][j];
            }
        }
        for(int i=3; i<4; i++){
            for(int j=0; j<FRAME_COLS; j++){
                walkRight[indexR++]=walkRegion[i][j];
            }
        }
        //Instantiate animations at duration 0.15f
        downAnim=new Animation<TextureRegion>(0.15f, walkDown);
        upAnim=new Animation<TextureRegion>(0.15f, walkUp);
        leftAnim=new Animation<TextureRegion>(0.15f, walkLeft);
        rightAnim=new Animation<TextureRegion>(0.15f, walkRight);
        stateTime=0f;
    }

    //Getter and Setter Methods
    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Texture getSprite() {
        return sprite;
    }

    public void setSprite(Texture sprite) {
        this.sprite = sprite;
    }
}
