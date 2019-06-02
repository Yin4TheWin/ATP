package com.nnmg.atp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.nnmg.atp.GameScreen;
import com.nnmg.atp.Main;

public class BattleScreen implements Screen {
    final Main game;
    private Music fight;
    private Texture enemyImg;
    private boolean slash;
    private Texture playerImg;
    private OrthographicCamera camera;
    private static final int FRAME_COLS=8, FRAME_ROWS=4;
    private Animation<TextureRegion> downAnim;
    private Animation<TextureRegion> upAnim;
    private Animation<TextureRegion> leftAnim;
    private Animation<TextureRegion> rightAnim;
    private Texture stayStill;
    private Texture walkSheet;
    private Texture sword;
    private float stateTime;
    private float stateTime1;
    private Texture tree;
    private Animation<TextureRegion>  swordAnims;
    Rectangle player;

    public BattleScreen(final Main game, Texture enemyImg, Texture playerImg){
        this.game=game;
        fight=Gdx.audio.newMusic(Gdx.files.internal("battle.ogg"));
        fight.setLooping(true);
        fight.play();
        slash=false;
        walkSheet=new Texture(Gdx.files.internal("walk.png"));
        stayStill=new Texture(Gdx.files.internal("static.png"));
        sword=new Texture(Gdx.files.internal("sword.png"));
        tree=new Texture(Gdx.files.internal("tree.png"));
        TextureRegion[][] swordRegion=TextureRegion.split(sword, sword.getWidth()/5, sword.getHeight());
        TextureRegion[] swordAnim=new TextureRegion[5];
        int indexS=0;
        for(int i=0;i<5;i++)
        {
            swordAnim[indexS++]=swordRegion[0][i];
        }
        swordAnims=new Animation<TextureRegion>(0.08f, swordAnim);
        this.enemyImg=enemyImg;
        this.playerImg=playerImg;
        this.enemyImg = new Texture(Gdx.files.internal("bucket.png"));
        this.playerImg = new Texture(Gdx.files.internal("staticRight.png"));
        //Spritesheet split up into 8x4
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
        stateTime1=0f;
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
        downAnim=new Animation<TextureRegion>(0.05f, walkDown);
        upAnim=new Animation<TextureRegion>(0.05f, walkUp);
        leftAnim=new Animation<TextureRegion>(0.05f, walkLeft);
        rightAnim=new Animation<TextureRegion>(0.05f, walkRight);
        stateTime=0f;
        player = new Rectangle();
        player.x = 832 / 2 - 64 / 2;
        player.y = 20;
        player.width = 48;
        player.height = 64;
        camera=new OrthographicCamera();
        camera.setToOrtho(false, 832, 512);
        camera.update();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        if(Gdx.input.isKeyPressed(Input.Keys.Z)){
            slash=true;
        }
        game.batch.begin();
        game.font.draw(game.batch, "This is a ", 305, 340);
        game.font.draw(game.batch, "Battle Screen.", 305, 240);
        game.batch.draw(enemyImg, 832-64-50, 225);
        if(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)){
            player.y += 300*Gdx.graphics.getDeltaTime();
            stayStill=new Texture(Gdx.files.internal("staticUp.png"));
            animateMove(upAnim);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)){
            player.x -= 300*Gdx.graphics.getDeltaTime();
            stayStill=new Texture(Gdx.files.internal("staticLeft.png"));
            animateMove(leftAnim);}
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)){player.y -= 300*Gdx.graphics.getDeltaTime();
            stayStill=new Texture(Gdx.files.internal("static.png"));
            animateMove(downAnim);}
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)){player.x += 300*Gdx.graphics.getDeltaTime();
            stayStill=new Texture(Gdx.files.internal("staticRight.png"));
            animateMove(rightAnim);}
        if(!(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)||Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)||Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)||Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)))
            game.batch.draw(stayStill, player.x, player.y);
        if(slash){
            stateTime1+= Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame;
            currentFrame = swordAnims.getKeyFrame(stateTime1,true);
            System.out.println(swordAnims.getKeyFrameIndex(stateTime1));
            game.batch.draw(currentFrame, player.x+44, player.y);
            if(swordAnims.getKeyFrameIndex(stateTime1)==4) {
                stateTime1=0;
                slash = false;
            }
        }
        game.batch.end();
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        System.out.println("pause");
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        fight.dispose();
        playerImg.dispose();
        enemyImg.dispose();
    }
    private void animateMove(Animation<TextureRegion> animate) {
        stateTime+= Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame = animate.getKeyFrame(stateTime, true);
        game.batch.draw(currentFrame, player.x, player.y);
    }
}
