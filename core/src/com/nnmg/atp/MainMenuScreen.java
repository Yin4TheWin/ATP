package com.nnmg.atp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {
    //Main is an object, it's kinda like the game itself if that makes sense
    final Main game;

    //Camera makes sure screen will always be certain size
    OrthographicCamera camera;

    public MainMenuScreen(final Main game) {
        //Don't worry too much about this
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 832, 512);

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        //Clears screen and makes it dark blue
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Refreshes screen to show updated sprites, I believe its 60 fps
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        //Actual drawing happens here
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to the Game! ", 305, 340);
        game.font.draw(game.batch, "Click the screen to begin.", 300, 240);
        game.batch.end();

        //Like an onClick
        if (Gdx.input.isTouched()) {
            //Goes to the GameScreen Class
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }
    //Don't worry about everything below this
    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
