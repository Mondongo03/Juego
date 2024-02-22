package com.ruben.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
	private Texture bucketImage;
	private Sound coinSound;
	private Sound bombSound;

	private Texture bombTexture;
	private Texture coinTexture;
	private Texture missileTexture;
	private Texture starTexture;

	private Music backgroundMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> bombdrops;
	private Array<Rectangle> bombs;
	private Array<Rectangle> explosions;
	private Array<Rectangle> missiles;
	private Array<Rectangle> stars;

	private long lastDropTime;
	private long lastDropTime2;
	private long lastDropTime3;
	private long lastDropTime4;

	private float stateTime;
	private int score,level;
	private String yourScoreName,Leveltxt;
	private BitmapFont yourBitmapFontName;

	float currentBgX;
	long lastTimeBg;

	private Stage stage,ab;
	private TextButton button,button2;
	private TextButton.TextButtonStyle textButtonStyle;
	private BitmapFont font;
	private Skin skin;
	private TextureAtlas buttonAtlas;

	Label ScoreLabel,LevelLabel,HiLabel;
	public boolean paused = false;

	@Override
	public void create() {
		BitmapFont font12=new BitmapFont(Gdx.files.internal("font.fnt"));
		score = 0;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();
		buttonAtlas = new TextureAtlas(Gdx.files.internal("button.pack"));
		skin.addRegions(buttonAtlas);
		textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = font12;

		button = new TextButton(" RESET SCORE ", textButtonStyle);
		button.setPosition(Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()-40);
		button.setScale(2,2);
		stage.addActor(button);



		currentBgX = 800;
		lastTimeBg = TimeUtils.nanoTime();


		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		background = new Texture(Gdx.files.internal("background.png"));
		background_2 = new Texture(Gdx.files.internal("background_2.png"));
		background_3 = new Texture(Gdx.files.internal("background_3.png"));
		background_4 = new Texture(Gdx.files.internal("background_4.png"));


		bombSound = Gdx.audio.newSound(Gdx.files.internal("bomb.mp3"));
		coinSound = Gdx.audio.newSound(Gdx.files.internal("coin.mp3"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("bg.mp3"));

		bombTexture = new Texture(Gdx.files.internal("bomb.png"));

		coinTexture = new Texture(Gdx.files.internal("coin.png"));


		missileTexture = new Texture(Gdx.files.internal("missile.png"));

		starTexture = new Texture(Gdx.files.internal("star.png"));

		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();






		texture = new Texture(Gdx.files.internal("background.png"));
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		level=1;
		yourScoreName = "Score:"+score;
		yourBitmapFontName = new BitmapFont();
		Leveltxt="Level:"+level;

		ScoreLabel = new Label(yourScoreName,new Label.LabelStyle(font12, Color.RED));
		ScoreLabel.setPosition(Gdx.graphics.getWidth()/2,20);
		ScoreLabel.setFontScale(2);
		stage.addActor(ScoreLabel);

		LevelLabel = new Label(Leveltxt,new Label.LabelStyle(font12, Color.BLACK));
		LevelLabel.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()-40);
		LevelLabel.setFontScale(2);
		stage.addActor( LevelLabel);

		Preferences pref = Gdx.app.getPreferences("CoinHuntHi");
		int hiscore=  pref.getInteger("hiscore", 0);
		String hiscoretxt="Hiscore:"+hiscore;

		HiLabel = new Label(hiscoretxt,new Label.LabelStyle(font12, Color.BLACK));
		HiLabel.setPosition(Gdx.graphics.getWidth()*3/4,Gdx.graphics.getHeight()-40);
		HiLabel.setFontScale(2);
		stage.addActor( HiLabel);






		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		bombdrops = new Array<Rectangle>();
		spawnCoin();

		bombs = new Array<Rectangle>();
		spawnBomb();
		explosions = new Array<Rectangle>();
		missiles = new Array<Rectangle>();
		if(level>1)spawnMissile();

		stars = new Array<Rectangle>();
		if(level>3)spawnStar();




	}


	private void spawnCoin() {
		Rectangle bombdrop = new Rectangle();
		bombdrop.x = MathUtils.random(0, 800 - 64);
		bombdrop.y = 480;
		bombdrop.width = 45;
		bombdrop.height = 45;
		bombdrops.add(bombdrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void spawnBomb() {
		Rectangle bomb = new Rectangle();
		bomb.x = MathUtils.random(0, 800 - 64);
		bomb.y = 480;
		bomb.width = 64;
		bomb.height = 64;
		bombs.add(bomb);
		lastDropTime2 = TimeUtils.nanoTime();
	}
	private void spawnMissile() {
		Rectangle missile = new Rectangle();
		missile.x = MathUtils.random(0, 800 - 64);
		missile.y = 480;
		missile.width = 64;
		missile.height = 64;
		missiles.add(missile);
		lastDropTime3 = TimeUtils.nanoTime();
	}

	private void spawnStar() {
		Rectangle star = new Rectangle();
		star.x = MathUtils.random(0, 800 - 64);
		star.y = 480;
		star.width = 70;
		star.height = 70;
		stars.add(star);
		lastDropTime4 = TimeUtils.nanoTime();
	}


	@Override
	public void render() {

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				score=0;
				yourScoreName = "Score:" + score;
				ScoreLabel.setText(yourScoreName);
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("score", score);
				prefs.flush();

				Preferences pref = Gdx.app.getPreferences("CoinHuntHi");
				pref.putInteger("hiscore", 0);
				pref.flush();


			}

		});

		Preferences pref = Gdx.app.getPreferences("CoinHuntHi");
		if(score> pref.getInteger("hiscore", 0)){
			pref.putInteger("hiscore", score);
			pref.flush();
		}
		int hiscore=  pref.getInteger("hiscore", 0);
		String hiscoretxt="Hiscore:"+hiscore;
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


		if(TimeUtils.nanoTime() - lastTimeBg > 100000000){
			currentBgX -= 50;
			lastTimeBg = TimeUtils.nanoTime();
		}
		if(currentBgX == 0){
			currentBgX = 800;
		}



		batch.draw(bucketImage, bucket.x, bucket.y);

		for (Rectangle bomb : bombs) {
			batch.draw(bombTexture, bomb.x, bomb.y, bomb.width, bomb.height);
		}
		for (Rectangle bombdrop : bombdrops) {
			batch.draw(coinTexture, bombdrop.x, bombdrop.y, bombdrop.width, bombdrop.height);
		}
		for (Rectangle missile : missiles) {
			batch.draw(missileTexture, missile.x, missile.y, missile.width, missile.height);
		}
		for (Rectangle star : stars) {
			batch.draw(starTexture, star.x, star.y, star.width, star.height);
		}





		yourBitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		yourBitmapFontName.draw(batch, yourScoreName, 25, 100);
		yourBitmapFontName.getData().setScale(2, 2);


		stage.draw();
		batch.end();


		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();


		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 800 - 64) bucket.x = 800 - 64;

		if (TimeUtils.nanoTime() - lastDropTime > 700000000) spawnCoin();
		if (TimeUtils.nanoTime() - lastDropTime2 > 1500000000) spawnBomb();
		if ((TimeUtils.nanoTime() - lastDropTime3 > 1000000000)&&level>1) spawnMissile();
		if ((TimeUtils.nanoTime() - lastDropTime3 > 500000000)&&level>2) spawnMissile();
		if ((TimeUtils.nanoTime() - lastDropTime3 > 500000000)&&level>3) spawnBomb();
		if ((TimeUtils.nanoTime() - lastDropTime > 500000000)&&level>3) spawnCoin();
		if ((TimeUtils.nanoTime() - lastDropTime > 350000000)&&level>7) spawnCoin();
		if ((TimeUtils.nanoTime() - lastDropTime4 > 1500000000)&&level>3) spawnStar();


		for (Iterator<Rectangle> iter = bombdrops.iterator(); iter.hasNext(); ) {
			Rectangle bombdrop = iter.next();
			bombdrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (bombdrop.y + 64 < 0) iter.remove();
			if (bombdrop.overlaps(bucket)) {
				score++;
				yourScoreName = "Score:" + score;
				ScoreLabel.setText(yourScoreName);

				coinSound.play();
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("score", score);
				prefs.flush();
				iter.remove();
			}
		}

		for (Iterator<Rectangle> iter = bombs.iterator(); iter.hasNext(); ) {
			Rectangle bomb = iter.next();
			bomb.y -= 200 * Gdx.graphics.getDeltaTime();
			if (bomb.y + 64 < 0) iter.remove();
			if (bomb.overlaps(bucket)) {
				score -= 5;
				if (score < 0) score = 0;
				yourScoreName = "Score:" + score;
				ScoreLabel.setText(yourScoreName);

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
			if (missile.overlaps(bucket)) {
				score -=20;
				if (score < 0) score = 0;
				yourScoreName = "Score:" + score;
				ScoreLabel.setText(yourScoreName);

				boom(3);
				Preferences prefs = Gdx.app.getPreferences("CoinHuntPreferences");
				prefs.putInteger("score", score);
				prefs.flush();
				iter.remove();
			}
		}

		for (Iterator<Rectangle> iter = stars.iterator(); iter.hasNext(); ) {
			Rectangle star = iter.next();
			star.y -= 400 * Gdx.graphics.getDeltaTime();
			if (star.y + 64 < 0) iter.remove();
			if (star.overlaps(bucket)) {
				score+=10;
				yourScoreName = "Score:" + score;
				ScoreLabel.setText(yourScoreName);

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
	private void boom(int intensity)
	{
		bombSound.play();

	}

	private int getgamelevel(int score)
	{
		int i = 1;
		if (score >=10){
			i=2;
		}
		if (score >=20){
			i=3;
		}
		if (score >=50){
			i=4;
		}
		if (score >=100){
			i=5;
		}
		return i;
	}



	@Override
	public void dispose() {

		background.dispose();
		bucketImage.dispose();
		coinSound.dispose();
		bombSound.dispose();
		backgroundMusic.dispose();
		batch.dispose();
	}
}
