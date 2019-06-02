package com.nnmg.atp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Main extends Game {

	public SpriteBatch batch;
	public BitmapFont font;

	public void create() {
	    //A group of sprites
		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();
		//Go to Main Menu class
		this.setScreen(new MainMenuScreen(this));
	}
//Don't worry about everything below this
	public void render() {
		super.render();
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}

}
