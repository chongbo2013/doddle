package com.dodles.app.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	private boolean firstRender = true;
	
	@Override
	public void create () {
		System.out.println("MyGdxGame::Create");
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		if (firstRender) {
			firstRender = false;
			System.out.println("MyGdxGame::render");
		}
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}

	@Override
	public final void resize(int newWidth, int newHeight) {
		System.out.println("MyGdxGame::resize W: " + newWidth + " H: " + newHeight);
	}

	@Override
	public void pause() {
		super.pause();
		System.out.println("MyGdxGame::pause");
	}

	@Override
	public void resume() {
		super.resume();
		System.out.println("MyGdxGame::resume");
	}

	@Override
	public void dispose () {
		System.out.println("MyGdxGame::Dispose");
		batch.dispose();
		img.dispose();
	}
}
