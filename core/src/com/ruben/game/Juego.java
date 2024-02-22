package com.ruben.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Juego extends ApplicationAdapter {
	private Texture background,background_2,background_3,background_4,bg,texture;
	private Sound coinSound;
	private Sound bombSound;
	private Texture sacoTexture;
	private Texture muerteTexture;
	private Texture bombTexture;
	private Texture coinTexture;
	private Texture missilTexture;
	private Texture starTexture;

	private Music backgroundMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle saco;
	private Array<Rectangle> monedas;
	private Array<Rectangle> bombas;
	private Array<Rectangle> muertes;
	private Array<Rectangle> missiles;
	private Array<Rectangle> lingotes;

	private long lastDropTimeCoin;
	private long lastDropTimeBomba;
	private long lastDropTimeMisil;
	private long lastDropTimeLingote;

	private float stateTime;
	private int score,level;
	private String yourScoreName,Leveltxt, textoVacio;
	private BitmapFont yourBitmapFontName;

	float currentBgX;
	long lastTimeBg;

	private Stage stage;
	private TextButton button;
	private TextButton.TextButtonStyle textButtonStyle;
	private Skin skin;
	Label scoreLabel,LevelLabel,HiLabel;
	public boolean paused = false;
	private long lastDropTimeMuerte;

	@Override
	public void create() {
		BitmapFont font12=new BitmapFont(Gdx.files.internal("font.fnt"));
		score = 0;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();
		textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = font12;

		button = new TextButton(" RESET SCORE ", textButtonStyle);
		button.setPosition(Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()-40);
		button.setScale(2,2);
		stage.addActor(button);

		currentBgX = 800;
		lastTimeBg = TimeUtils.nanoTime();

		sacoTexture = new Texture(Gdx.files.internal("saco_monedas.png"));
		background = new Texture(Gdx.files.internal("images.jpg"));
		background_2 = new Texture(Gdx.files.internal("background.jpg"));
		background_3 = new Texture(Gdx.files.internal("background3.png"));
		background_4 = new Texture(Gdx.files.internal("background_4.png"));

		bombSound = Gdx.audio.newSound(Gdx.files.internal("bomb.mp3"));
		coinSound = Gdx.audio.newSound(Gdx.files.internal("coin.mp3"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bg.mp3"));
		bombTexture = new Texture(Gdx.files.internal("bomb.png"));
		coinTexture = new Texture(Gdx.files.internal("coin.png"));
		muerteTexture = new Texture(Gdx.files.internal("skull.png"));
		missilTexture = new Texture(Gdx.files.internal("missile.png"));

		starTexture = new Texture(Gdx.files.internal("star.png"));

		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();

		texture = new Texture(Gdx.files.internal("background.jpg"));

		level=1;
		yourScoreName = "Puntuación: "+score;
		yourBitmapFontName = new BitmapFont();
		Leveltxt="Level:"+level;

		scoreLabel = new Label("", new Label.LabelStyle(font12, Color.RED));
		scoreLabel.setPosition(Gdx.graphics.getWidth() / 2, 20);
		scoreLabel.setFontScale(2);
		stage.addActor(scoreLabel);

		LevelLabel = new Label(Leveltxt,new Label.LabelStyle(font12, Color.RED));
		LevelLabel.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()-40);
		LevelLabel.setFontScale(2);
		stage.addActor( LevelLabel);

		Preferences pref = Gdx.app.getPreferences("CoinHuntHi");
		int hiscore=  pref.getInteger("hiscore", 0);
		String hiscoretxt="Hiscore:"+hiscore;

		HiLabel = new Label(hiscoretxt,new Label.LabelStyle(font12, Color.RED));
		HiLabel.setPosition(Gdx.graphics.getWidth()*3/4,Gdx.graphics.getHeight()-40);
		HiLabel.setFontScale(2);
		stage.addActor( HiLabel);

		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		saco = new Rectangle();
		saco.x = 800 / 2 - 64 / 2;
		saco.y = 20;
		saco.width = 64;
		saco.height = 64;

		monedas = new Array<Rectangle>();
		spawnCoin();
		bombas = new Array<Rectangle>();
		spawnBomb();
		missiles = new Array<Rectangle>();
		if(level>1)spawnMissile();
		lingotes = new Array<Rectangle>();
		if(level>3) spawnLingote();
		muertes = new Array<Rectangle>();


	}

	private void spawnCoin() {
		Rectangle bombdrop = new Rectangle();
		bombdrop.x = MathUtils.random(0, 800 - 64);
		bombdrop.y = 480;
		bombdrop.width = 45;
		bombdrop.height = 45;
		monedas.add(bombdrop);
		lastDropTimeCoin = TimeUtils.nanoTime();
	}

	private void spawnBomb() {
		Rectangle bomb = new Rectangle();
		bomb.x = MathUtils.random(0, 800 - 64);
		bomb.y = 480;
		bomb.width = 64;
		bomb.height = 64;
		bombas.add(bomb);
		lastDropTimeBomba = TimeUtils.nanoTime();
	}
	private void spawnMissile() {
		Rectangle missile = new Rectangle();
		missile.x = MathUtils.random(0, 800 - 64);
		missile.y = 480;
		missile.width = 64;
		missile.height = 64;
		missiles.add(missile);
		lastDropTimeMisil = TimeUtils.nanoTime();
	}
	private void spawnMuertes() {
		Rectangle muerte = new Rectangle();
		muerte.x = MathUtils.random(0, 800 - 64);
		muerte.y = 480;
		muerte.width = 45;
		muerte.height = 45;
		muertes.add(muerte);
		lastDropTimeMuerte = TimeUtils.nanoTime();
	}

	private void spawnLingote() {
		Rectangle lingote = new Rectangle();
		lingote.x = MathUtils.random(0, 800 - 64);
		lingote.y = 480;
		lingote.width = 70;
		lingote.height = 70;
		lingotes.add(lingote);
		lastDropTimeLingote = TimeUtils.nanoTime();
	}

	@Override
	public void render() {

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				score=0;
				yourScoreName = "Puntuación: " + score;
				scoreLabel.setText(yourScoreName);
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("Puntuación ", score);
				prefs.flush();

				Preferences pref = Gdx.app.getPreferences("CoinHuntHi");
				pref.putInteger("Puntuación Máxima", 0);
				pref.flush();


			}

		});

		scoreLabel.setText("Puntuación: " + score);


		Preferences pref = Gdx.app.getPreferences("CoinHuntHi");
		if(score> pref.getInteger("Puntuación Máxima", 0)){
			pref.putInteger("Puntuación Máxima", score);
			pref.flush();
		}
		int hiscore=  pref.getInteger("Puntuación Máxima", 0);
		String hiscoretxt="Puntuación Máxima "+hiscore;
		HiLabel.setText(hiscoretxt);

		stateTime += Gdx.graphics.getDeltaTime();

		camera.update();

		if(level==1)bg=background;
		if(level==2)bg=background_2;
		if(level==3)bg=background_3;
		if(level>3)bg=background_4;

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(bg, currentBgX - 800, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.draw(bg, currentBgX, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		batch.draw(sacoTexture, saco.x, saco.y);

		for (Rectangle bomb : bombas) {
			batch.draw(bombTexture, bomb.x, bomb.y, bomb.width, bomb.height);
		}
		for (Rectangle bombdrop : monedas) {
			batch.draw(coinTexture, bombdrop.x, bombdrop.y, bombdrop.width, bombdrop.height);
		}
		for (Rectangle missile : missiles) {
			batch.draw(missilTexture, missile.x, missile.y, missile.width, missile.height);
		}
		for (Rectangle lingote : lingotes) {
			batch.draw(starTexture, lingote.x, lingote.y, lingote.width, lingote.height);
		}
		for (Rectangle muerte : muertes) {
			batch.draw(muerteTexture, muerte.x, muerte.y, muerte.width, muerte.height);
		}
		textoVacio = "";

		yourBitmapFontName.setColor(0f, 0f, 0f, 0f);
		yourBitmapFontName.draw(batch, yourScoreName, 25, 100);
		yourBitmapFontName.getData().setScale(2, 2);


		stage.draw();
		batch.end();


		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			saco.x = touchPos.x -30;
		}




		if (TimeUtils.nanoTime() - lastDropTimeCoin > 700000000) spawnCoin();
		if (TimeUtils.nanoTime() - lastDropTimeBomba > 1500000000) spawnBomb();
		if (TimeUtils.nanoTime() - lastDropTimeMuerte > 6000000000L) spawnMuertes();
		if ((TimeUtils.nanoTime() - lastDropTimeMisil > 1000000000)&&level>1) spawnMissile();
		if ((TimeUtils.nanoTime() - lastDropTimeMisil > 500000000)&&level>2) spawnMissile();
		if ((TimeUtils.nanoTime() - lastDropTimeMisil > 500000000)&&level>3) spawnBomb();
		if ((TimeUtils.nanoTime() - lastDropTimeCoin > 500000000)&&level>3) spawnCoin();
		if ((TimeUtils.nanoTime() - lastDropTimeCoin > 350000000)&&level>7) spawnCoin();
		if ((TimeUtils.nanoTime() - lastDropTimeLingote > 1500000000)&&level>3) spawnLingote();


		for (Iterator<Rectangle> iter = monedas.iterator(); iter.hasNext(); ) {
			Rectangle coin = iter.next();
			coin.y -= 200 * Gdx.graphics.getDeltaTime();
			if (coin.y + 64 < 0) iter.remove();
			if (coin.overlaps(saco)) {
				score++;
				yourScoreName = "Puntuación: " + score;
				scoreLabel.setText(yourScoreName);

				coinSound.play();
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("Puntuación: ", score);
				prefs.flush();
				iter.remove();
			}
		}

		for (Iterator<Rectangle> iter = bombas.iterator(); iter.hasNext(); ) {
			Rectangle bomb = iter.next();
			bomb.y -= 200 * Gdx.graphics.getDeltaTime();
			if (bomb.y + 64 < 0) iter.remove();
			if (bomb.overlaps(saco)) {
				score -= 5;
				if (score < 0) score = 0;
				yourScoreName = "Score:" + score;
				scoreLabel.setText(yourScoreName);

				boom(2);
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("score", score);
				prefs.flush();
				iter.remove();
			}
		}

		for (Iterator<Rectangle> iter = missiles.iterator(); iter.hasNext(); ) {
			Rectangle missile = iter.next();
			missile.y -= 400 * Gdx.graphics.getDeltaTime();
			if (missile.y + 64 < 0) iter.remove();
			if (missile.overlaps(saco)) {
				score -=20;
				if (score < 0) score = 0;
				yourScoreName = "Score:" + score;
				scoreLabel.setText(yourScoreName);

				boom(3);
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("score", score);
				prefs.flush();
				iter.remove();
			}
		}
		for (Iterator<Rectangle> iter = muertes.iterator(); iter.hasNext(); ) {
			Rectangle muerte = iter.next();
			muerte.y -= 200 * Gdx.graphics.getDeltaTime();
			if (muerte.y + 64 < 0) iter.remove();
			if (muerte.overlaps(saco)) {
				score = 0;
				yourScoreName = "Puntuación: " + score;
				scoreLabel.setText(yourScoreName);

				coinSound.play();
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("Puntuación: ", score);
				prefs.flush();
				iter.remove();
			}
		}

		for (Iterator<Rectangle> iter = lingotes.iterator(); iter.hasNext(); ) {
			Rectangle star = iter.next();
			star.y -= 400 * Gdx.graphics.getDeltaTime();
			if (star.y + 64 < 0) iter.remove();
			if (star.overlaps(saco)) {
				score+=10;
				yourScoreName = "Score:" + score;
				scoreLabel.setText(yourScoreName);

				coinSound.play();
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("score", score);
				prefs.flush();
				iter.remove();
			}
		}

		level=getgamelevel(score) ;
		LevelLabel.setText("Level:"+level);

		if (!paused) {
			Gdx.graphics.requestRendering();
		}

	}
	private void boom(int intensity){
		bombSound.play();
	}

	private int getgamelevel(int score)
	{
		int i = 1;
		if (score >=10) i=2;

		if (score >=20) i=3;

		if (score >=25) i=4;

		if (score >=27)i=5;

		return i;
	}

	@Override
	public void dispose() {
		background.dispose();
		sacoTexture.dispose();
		coinSound.dispose();
		bombSound.dispose();
		backgroundMusic.dispose();
		batch.dispose();
	}
}