package com.nnmg.atp;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import sun.rmi.runtime.Log;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;

    private SpriteBatch batch;
    private Texture walkSheet;
    private Texture enemyImage;
    private Texture npcImage;
    private Music theme;
    private Sound hit;
    private OrthographicCamera camera;
    private Array<Rectangle> enemies;
    private Array<Rectangle> talkers;
    private int one;
    private int two;
    private TextButton tb;
    private boolean isTalking;
    private boolean talk;
    private boolean talkHelper;
    private TiledMapRenderer tiledMapRenderer;
    private Preferences prefs;
    private Texture stayStill;
    private float stateTime;
    private int push;
    int objectLayerId;
    boolean collide;
    Player p;
    MapObjects objects;

    @SuppressWarnings("GwtInconsistentSerializableClass")
    public enum NPC{
        BOB("Bob", new Texture(Gdx.files.internal("sensei.png")), "You have a long way to go, young one.");
        String name;
        String dialogue;
        Texture image;
        NPC(String name, Texture image, String dialogue) {
            this.name=name;
            this.dialogue=dialogue;
            this.image=image;

        }

        public String getDialogue() {
            return dialogue;
        }

        public String getName() {
            return name;
        }

        public Texture getImage() {
            return image;
        }
    }
    NPC bob;
    public GameScreen (final Main game) {
        //Will be used to count seconds (for autosave)
        push=0;
        bob= NPC.BOB;
        collide=false;
        //Two booleans used to control NPC talking
        talkHelper=true;
        talk=false;
        objectLayerId = 4;
        //Preferences, used to save the game
        prefs=Gdx.app.getPreferences("My Preferences");
        talkers=new Array<Rectangle>();
        //Instantiate images. Walk is a spritesheet
        walkSheet=new Texture(Gdx.files.internal("walk.png"));
        stayStill=new Texture(Gdx.files.internal("static.png"));

        MyInputProcessor inputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        //Add each individual sprite from each row into an array for animating (Don't worry about this)

        //Instantiate animations at duration 0.15f
        isTalking=false;
        this.game=game;
        //This is for loading maps. To see a full documentation on how maps are loaded check the "Wiki" tab on the Github.
        one=prefs.getInteger("one",1);
        two=prefs.getInteger("two", 0);
        //Don't worry about this
        camera=new OrthographicCamera();
        camera.setToOrtho(false, 832, 512);
        camera.update();


        //Instantiates batch, a group of sprites
        batch = new SpriteBatch();
        //Instantiates player and enemy images
        enemyImage = new Texture(Gdx.files.internal("bucket.png"));
        npcImage = new Texture(Gdx.files.internal("sensei.png"));
        //Instantiate NPC hitbox
        if(one==1&&two==0)
            spawnNPC();
        //Sounds
        hit=Gdx.audio.newSound(Gdx.files.internal("woosh.mp3"));
        theme=Gdx.audio.newMusic(Gdx.files.internal("Windless Slopes.ogg"));

        theme.setLooping(true);
        theme.play();

        //Instantiates hitboxes for player and enemies
        p=new Player(new Rectangle(), 48,64, new Texture(Gdx.files.internal("static.png")));
        p.rectangle.x = prefs.getFloat("playerx", 20);
        p.rectangle.y = prefs.getFloat("playery", 300);
        p.setWidth(48);
        p.setHeight(64);

        //Array of enemy hitboxes
        enemies = new Array<Rectangle>();
        //Go to spawn enemy method
        spawnEnemy();

        //For more information, see the Wiki on the Github
        loadMap(one, two);

        createText("Yeet");

    }

    @Override
    public void render (float delta) {
        //Clears screen
        Gdx.gl.glClearColor(0.2f, 1, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Refreshes screen, I believe it's 60 fps
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        //Don't worry about this
        batch.setProjectionMatrix(camera.combined);
        //Begin drawing
        batch.begin();
        //Draw NPC at NPC position
        if(one==1&&two==0) {
            for(Rectangle r:talkers)
                batch.draw(bob.image, r.x, r.y);
        }
        //Draw enemies at their respective positions
        for(Rectangle enemy: enemies) {
            batch.draw(enemyImage, enemy.x, enemy.y);
        }
        //Draws a textbox if isTalking is true
        if (isTalking) {
            tb.draw(batch, 1.0f);
        }
        //Player movement script. I use two booleans, talk and isTalking, to control player movement. Don't worry too much about
        //because I'm pretty sure this is bug-free now.
        if(talk)
            tryTalking();
        if(!isTalking){
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)){
                //Loops through next 200 pixels individually to check collisions to allow pixel perfect ones
                for(int x=0; x<400;x++) {
                    if(talkers.size>0) {
                        for (Rectangle r : talkers) {
                            if (new Rectangle(p.rectangle.x - 1, p.rectangle.y, 48f, 64f).overlaps(r)) {
                                talk = true;
                            } else {
                                if(!isColliding(new Rectangle(p.rectangle.x - 1, p.rectangle.y, 48f, 64f)))
                                    p.rectangle.x -= Gdx.graphics.getDeltaTime()/2;
                                talk = false;
                                prefs.putFloat("playerx", p.rectangle.x);
                            }
                        }
                    }
                    else{
                        if(!isColliding(new Rectangle(p.rectangle.x - 1, p.rectangle.y, 48f, 64f)))
                            p.rectangle.x -= Gdx.graphics.getDeltaTime()/2;
                        talk = false;
                        prefs.putFloat("playerx", p.rectangle.x);
                    }
                }
                //Changes default image to face left if you stop moving
                stayStill=new Texture(Gdx.files.internal("staticLeft.png"));
                if(!(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)||Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)||Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S))){
                    //Walk left
                    animateMove(p.leftAnim);
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)){
                //Loops through next 200 pixels individually to check collisions to allow pixel perfect ones
                for(int x=0; x<400;x++) {
                    if(talkers.size>0) {
                        for (Rectangle r : talkers) {
                            if (new Rectangle(p.rectangle.x, p.rectangle.y - 1, 48f, 64f).overlaps(r)) {
                                talk = true;
                            } else {
                                if(!isColliding(new Rectangle(p.rectangle.x, p.rectangle.y - 1, 48f, 64f)))
                                    p.rectangle.y -= Gdx.graphics.getDeltaTime()/2;
                                talk = false;
                                prefs.putFloat("playery", p.rectangle.y);
                            }
                        }
                    }
                    else{
                        if(!isColliding(new Rectangle(p.rectangle.x, p.rectangle.y - 1, 48f, 64f)))
                            p.rectangle.y -= Gdx.graphics.getDeltaTime()/2;
                        talk = false;
                        prefs.putFloat("playery", p.rectangle.y);
                    }
                }
                stayStill=new Texture(Gdx.files.internal("static.png"));
                if(!(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)||Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D))){
                    animateMove(p.downAnim);
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)){
                //Loops through next 200 pixels individually to check collisions to allow pixel perfect ones
                for(int x=0; x<400;x++) {
                    if(talkers.size>0) {
                        for (Rectangle r : talkers) {
                            if (new Rectangle(p.rectangle.x + 1, p.rectangle.y, 48f, 64f).overlaps(r)) {
                                talk = true;
                            } else {
                                if(!isColliding(new Rectangle(p.rectangle.x + 1, p.rectangle.y, 48f, 64f)))
                                    p.rectangle.x += Gdx.graphics.getDeltaTime()/2;
                                talk = false;
                                prefs.putFloat("playerx", p.rectangle.x);
                            }
                        }
                    }
                    else{
                        if(!isColliding(new Rectangle(p.rectangle.x + 1, p.rectangle.y, 48f, 64f)))
                            p.rectangle.x += Gdx.graphics.getDeltaTime()/2;
                        talk = false;
                        prefs.putFloat("playerx", p.rectangle.x);
                    }
                }
                stayStill=new Texture(Gdx.files.internal("staticRight.png"));
                if(!(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W))){
                    animateMove(p.rightAnim);
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)){
                //Loops through next 200 pixels individually to check collisions to allow pixel perfect ones
                for(int x=0; x<400;x++) {
                    if(talkers.size>0) {
                        for (Rectangle r : talkers) {
                            if (new Rectangle(p.rectangle.x, p.rectangle.y + 1, 48f, 64f).overlaps(r)) {
                                talk = true;
                            } else {
                                if(!isColliding(new Rectangle(p.rectangle.x, p.rectangle.y + 1, 48f, 64f)))
                                    p.rectangle.y += Gdx.graphics.getDeltaTime()/2;
                                talk = false;
                                prefs.putFloat("playery", p.rectangle.y);
                            }
                        }
                    }
                    else{
                        if(!isColliding(new Rectangle(p.rectangle.x, p.rectangle.y + 1, 48f, 64f)))
                            p.rectangle.y += Gdx.graphics.getDeltaTime()/2;
                        talk = false;
                        prefs.putFloat("playery", p.rectangle.y);
                    }
                }
                stayStill=new Texture(Gdx.files.internal("staticUp.png"));
                animateMove(p.upAnim);
            }
            if(!(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)||Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)||Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)||Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)))
               //If no keys are being pressed, stand still.
                batch.draw(stayStill, p.rectangle.x, p.rectangle.y);
        }
        else
            //If being talked to, stand still
            batch.draw(stayStill, p.rectangle.x, p.rectangle.y);
        batch.end();

        //Make sure you don't go out of bounds and will teleport you to the next map.
        if(p.rectangle.x < 0){
            one-=2;
            if(Gdx.files.internal("test"+one+""+two+".tmx").exists()) {
                p.rectangle.x = 832-64;
                loadMap(one, two);
                talkers.clear();
                if(one==1&&two==0)
                    spawnNPC();
            }
            else{
                one+=2;
                checkBounds(p.getRectangle());
            }
            prefs.putInteger("one", one);
            prefs.putInteger("two", two);
        }
        if(p.rectangle.x > 832 - 64){
            one+=2;
            if(Gdx.files.internal("test"+one+""+two+".tmx").exists()) {
                p.rectangle.x = 0;
                loadMap(one, two);
                talkers.clear();
                if(one==1&&two==0)
                    spawnNPC();
            }
            else{
                one-=2;
                checkBounds(p.getRectangle());
            }
            prefs.putInteger("one", one);
            prefs.putInteger("two", two);
        }
        if(p.rectangle.y < 0){
            one--;
            two--;
            if(Gdx.files.internal("test"+one+""+two+".tmx").exists()) {
                p.rectangle.y = 512-64;
                loadMap(one, two);
                talkers.clear();
                if(one==1&&two==0)
                    spawnNPC();
            }
            else{
                one++;
                two++;
                checkBounds(p.getRectangle());
            }
            prefs.putInteger("one", one);
            prefs.putInteger("two", two);
        }
        if(p.rectangle.y > 512 - 64){
            one++;
            two++;
            if(Gdx.files.internal("test"+one+""+two+".tmx").exists()) {
                p.rectangle.y = 0;
                loadMap(one, two);
                talkers.clear();
                if(one==1&&two==0)
                    spawnNPC();
            }
            else{
                one--;
                two--;
                checkBounds(p.getRectangle());
            }
            prefs.putInteger("one", one);
            prefs.putInteger("two", two);
        }
        //Moves enemies. It loops through each enemy hitbox (a rectangle) and there is a 1/300 chance it will move a certain direction.
        //Since this is probably 60 fps this results in the enemy moving in a random direction approximately once every 5 seconds.
        Iterator<Rectangle> iter = enemies.iterator();
        while(iter.hasNext()){
            //Move to next rectangle
            Rectangle enemy = iter.next();
            if(MathUtils.random(0,350)==1)
                enemy.y-=2000*Gdx.graphics.getDeltaTime();
            else if(MathUtils.random(0,350)==2)
                enemy.x-=2000*Gdx.graphics.getDeltaTime();
            else if(MathUtils.random(0,350)==3)
                enemy.x+=2000*Gdx.graphics.getDeltaTime();
            else if(MathUtils.random(0,350)==4)
                enemy.y+=2000*Gdx.graphics.getDeltaTime();
            if(enemy.overlaps(p.getRectangle())){
                game.setScreen(new BattleScreen(game, enemyImage, walkSheet));
                prefs.flush();
                iter.remove();
                dispose();
            }

            //Make sure enemy doesn't leave screen
            checkBounds(enemy);

            //Saves your location every five seconds. Not optimal at all.
            push++;
            if(push%300==0)
                prefs.flush();
        }
    }

    private void animateMove(Animation<TextureRegion> animate) {
        stateTime+= Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame = animate.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, p.rectangle.x, p.rectangle.y);
    }

    private void checkBounds(Rectangle object) {
        if(object.x < 0) object.x = 0;
        if(object.x > 832 - 64) object.x = 832 - 64;
        if(object.y < 0) object.y = 0;
        if(object.y > 512 - 64) object.y = 512 - 64;
    }
    public void spawnNPC(){
        Rectangle npcBox;
        npcBox=new Rectangle();
        npcBox.x = 200;
        npcBox.y = 270;
        npcBox.width = 36;
        npcBox.height = 69;
        talkers.add(npcBox);
    }
    private void spawnEnemy() {
        //Spawns 3 random enemies
        for(int x=0; x<3; x++) {
            Rectangle enemy = new Rectangle();
            //Spawn at random x and y coords
            enemy.x = MathUtils.random(100, 732 - 64);
            enemy.y = MathUtils.random(50, 482 - 64);
            if(Math.abs(enemy.x-p.rectangle.x)<=30)
                enemy.x+=30;
            if(Math.abs(enemy.x-p.rectangle.y)<=30)
                enemy.y-=30;
            enemy.width = 64;
            enemy.height = 64;
            enemies.add(enemy);
        }
    }
    public void tryTalking(){
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            // Use a helper so that a held-down button does not continuously switch between states with every tick
            if (talkHelper) {
                System.out.print(isTalking+", ");
                if (isTalking) {
                    isTalking=false;
                }
                else {
                    isTalking = true;
                }
                System.out.println(isTalking);
                talkHelper = false;
            }
        }
        else
            talkHelper=true;
    }

    private void loadMap(int mapNo1, int mapNo2){
        TiledMap newMap;
        newMap = new TmxMapLoader().load("test"+mapNo1+""+mapNo2+".tmx");
        TiledMap map = newMap;
            tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
            enemies.clear();
            spawnEnemy();
        //collisionObjectLayer = (TiledMapTileLayer)map.getLayers().get(2);
        System.out.println(map.getLayers().getCount()+"");
        objects = map.getLayers().get("Object Layer 1").getObjects();
        System.out.println(objects.getCount());
        System.out.println(map.getLayers().get("Object Layer 1").getObjects().toString());
    }
    //Don't worry about everything below this
    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose () {
        batch.dispose();
        walkSheet.dispose();
        enemyImage.dispose();
        theme.dispose();
    }
    public boolean isColliding(Rectangle r){
            for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
                Rectangle rectanglE = rectangleObject.getRectangle();
               // System.out.println("E");
                if(Intersector.overlaps(rectanglE, r)) {
                    System.out.println("collided");
                    return true;
                }
            }
        return false;
    }
    private void createText(String text)
    {
        //Don't worry about this. All you need to know is if you want to create a textbox use createText("Hello World");
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", new Color(0, 0, 0, 0.7f));
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        tb = new TextButton(text, skin);
        tb.setX(0);
        tb.setY(0);
        tb.setWidth(Gdx.graphics.getWidth());
        tb.setHeight(120);
        tb.setVisible(true);
    }
    public class MyInputProcessor implements InputProcessor {
        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            switch(keycode){
                case Input.Keys.
                        UP:
                    p.rectangle.y=((p.rectangle.y + 63) / 64 ) * 64;
                case Input.Keys
                            .DOWN:
                    p.rectangle.y-=p.rectangle.y%64;
                case Input.Keys.LEFT:
                    p.rectangle.x-=p.rectangle.x%64;
                case Input.Keys.RIGHT:
                    p.rectangle.x=((p.rectangle.x + 63) / 64 ) * 64;
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }

}
