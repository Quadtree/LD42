package info.quadtree.ld42;

import com.badlogic.gdx.*;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import info.quadtree.ld42.unit.Unit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class LD42 extends ApplicationAdapter implements InputProcessor {
	public SpriteBatch batch;
	Texture img;

	TextureAtlas atlas;

	public static LD42 s;

	public GameState gs;

	Map<String, Sprite> assetMap = new HashMap<>();

	public Sprite getSprite(String name){
		if (!assetMap.containsKey(name)) assetMap.put(name, atlas.createSprite(name));

		return assetMap.get(name);
	}

	Label.LabelStyle defaultLabelStyle;
	Label.LabelStyle backgroundLabelStyle;

	Stage uiStage;

	Label infoLabel;

	Label winLabel;

	Label[] teamLabels;
	Label[] teamScoreLabels;

	public BitmapFont defaultFont;

	public Sound shoot;
	public Sound capture;
	public Sound explosion;
	public Sound plop;
	public Sound detach;

	public boolean titleScreenUp = true;

	Matrix4 origMat;

	public Stage backgroundCloudStage;

	public BitmapFont titleFont;

	Window controlBar;

	Button.ButtonStyle buttonStyle;

	TextButton.TextButtonStyle textButtonStyle;
	
	@Override
	public void create () {
		LD42.s = this;
		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		dialogNinePatch = atlas.createPatch("dialog");

		origMat = new Matrix4(batch.getTransformMatrix());

		defaultFont = new BitmapFont(Gdx.files.internal("orbitron16.fnt"));
		titleFont = new BitmapFont(Gdx.files.internal("orbitron90.fnt"));

		gs = new GameState();
		gs.generate();



		defaultLabelStyle = new Label.LabelStyle(defaultFont, Color.WHITE);
		backgroundLabelStyle = new Label.LabelStyle(defaultFont, Color.WHITE);
		backgroundLabelStyle.background = new TextureRegionDrawable(getSprite("partial"));

		uiStage = new Stage();
		infoLabel = new Label("TEST", defaultLabelStyle);

		InputMultiplexer mp = new InputMultiplexer();
		mp.addProcessor(uiStage);
		mp.addProcessor(this);
		Gdx.input.setInputProcessor(mp);

		Table scoreTable = new Table();
		teamLabels = new Label[gs.turnOrder.size()];
		teamScoreLabels = new Label[gs.turnOrder.size()];

		for (int i=0;i<4;++i){
			Label teamLabel = new Label("", defaultLabelStyle);
			Label teamScoreLabel = new Label("", defaultLabelStyle);
			teamLabels[i] = teamLabel;
			teamScoreLabels[i] = teamScoreLabel;

			scoreTable.add(teamLabel).fill().left();
			scoreTable.add(teamScoreLabel).padLeft(20);
			scoreTable.row();
		}

		Util.windowStyle = new Window.WindowStyle(LD42.s.defaultFont, Color.WHITE, new NinePatchDrawable(LD42.s.dialogNinePatch));

		textButtonStyle = new TextButton.TextButtonStyle(new NinePatchDrawable(atlas.createPatch("button_up")), new NinePatchDrawable(atlas.createPatch("button_down")), new NinePatchDrawable(atlas.createPatch("button_down")), defaultFont);


		controlBar = new Window("", Util.windowStyle);

		uiStage.addActor(controlBar);
		controlBar.setBounds(0, 0, Gdx.graphics.getWidth(), 100);

		controlBar.add(scoreTable);
		controlBar.add(infoLabel).pad(10);

		buttonStyle = new Button.ButtonStyle(new NinePatchDrawable(atlas.createPatch("button_up")), new NinePatchDrawable(atlas.createPatch("button_down")), new NinePatchDrawable(atlas.createPatch("button_down")));

		Table buyBar = new Table();

		Stream.of(Unit.UnitType.Mine, Unit.UnitType.Tank, Unit.UnitType.Scout, Unit.UnitType.Turret).forEach(it -> {
			Button b = new Button(buttonStyle);

			Unit theUnit = Unit.factory(it);

			b.add(new Label(theUnit.getName(), defaultLabelStyle));
			b.row();
			b.add(new Image(new OverlayTextureRegion(
					new TextureRegionDrawable(getSprite(theUnit.getMainGraphicName())),
					getSprite(theUnit.getFlagGraphicName())
			)));
			buyBar.add(b).padRight(10);
		});

		controlBar.add(buyBar);

		TextButton endTurnButton = new TextButton("End Turn", textButtonStyle);
		controlBar.add(endTurnButton);

		winLabel = new Label("", defaultLabelStyle);
		winLabel.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
		uiStage.addActor(winLabel);

		shoot = Gdx.audio.newSound(Gdx.files.internal("Laser_Shoot62.wav"));
		capture = Gdx.audio.newSound(Gdx.files.internal("Powerup8.wav"));
		explosion = Gdx.audio.newSound(Gdx.files.internal("Explosion322.wav"));
		plop = Gdx.audio.newSound(Gdx.files.internal("landing.wav"));
		detach = Gdx.audio.newSound(Gdx.files.internal("Explosion339.wav"));

		backgroundCloudStage = new Stage();

		for (int i=0;i<20;++i){
			backgroundCloudStage.addActor(new BackgroundCloud());
		}

		/*for (int x=5;x<9;++x){
			for (int y=5;y<15;++y){
				gs.setHex(new Hex(x,y,20));
			}
		}*/

		/*gs.setHex(new Hex(5,5, 50));

		gs.setHex(new Hex(5,4, 20));
		gs.setHex(new Hex(6,4, 20));
		gs.setHex(new Hex(5,3, 20));
		gs.setHex(new Hex(5,6, 20));
		gs.setHex(new Hex(6,6, 20));
		gs.setHex(new Hex(5,7, 20));

		gs.setHex(new Hex(5,12, 10));

		gs.setHex(new Hex(4,11, 20));
		gs.setHex(new Hex(5,11, 20));
		gs.setHex(new Hex(5,10, 20));
		gs.setHex(new Hex(4,13, 20));
		gs.setHex(new Hex(5,13, 20));
		gs.setHex(new Hex(5,14, 20));*/

		/*gs.setHex(new Hex(5,5, 50));
		Arrays.stream(gs.getHex(5,5).getNeighbors()).forEach(it -> {
			gs.setHex(new Hex(it.x, it.y, 20));
		});

		gs.setHex(new Hex(5,12, 10));
		Arrays.stream(gs.getHex(5,12).getNeighbors()).forEach(it -> {
			gs.setHex(new Hex(it.x, it.y, 20));
		});*/
	}

	int moves;

	float titleMove = -800;

	boolean resetInProgress = false;

	NinePatch dialogNinePatch;

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.7f, 0.7f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setTransformMatrix(new Matrix4(origMat));
		batch.getTransformMatrix().translate(0, titleMove, 0);
		titleMove += 500f * Gdx.graphics.getDeltaTime();

		if (titleMove > 0 && !resetInProgress){
			titleMove = 0;
		}

		if (resetInProgress){
			if (gs.hexStream().count() == 0){
				startOrRestart();
			} else {
				titleMove += 500f * Gdx.graphics.getDeltaTime();
				if (titleMove > 800){
					startOrRestart();
				}
			}
		}

		if (titleScreenUp){
			if (gs.turnNum > 50){
				gs.dispose();
				gs = new GameState();
				gs.generate();
				titleMove = -800;
			}

			gs.turnOrder.remove(Team.Overminers);
			gs.turnOrder.remove(Team.DigCorp);
			gs.turnOrder.remove(Team.InterstellarElectric);
			gs.turnOrder.remove(Team.Underminers);
			gs.currentTurnTeam = Team.Nobody;

			backgroundCloudStage.act();
			backgroundCloudStage.draw();

			batch.begin();
			gs.render(titleMove >= 0);
			batch.end();

			batch.setTransformMatrix(new Matrix4(origMat));
			batch.begin();

			GlyphLayout gl = new GlyphLayout();
			gl.setText(titleFont, "Overminers Inc.");
			titleFont.draw(batch, gl, Gdx.graphics.getWidth() / 2f - gl.width / 2, Gdx.graphics.getHeight() - 100);

			gl.setText(defaultFont, "Press any key to start");
			defaultFont.draw(batch, gl, Gdx.graphics.getWidth() / 2f - gl.width / 2, 500f);

			defaultFont.draw(batch, "Made by Quadtree for Ludum Dare 42", Gdx.graphics.getWidth() - 450, 20);

			batch.end();

			return;
		}

		gs.hexStream().forEach(it -> it.shadowToRender = null);
		if (gs.selectedUnitTypeToPlace != null) {
			gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()).ifPresent(destHex -> {
				destHex.shadowToRender = gs.selectedUnitTypeToPlace;
			});
		}


		for (int i=0;i<4;++i){
			String name = gs.turnOrder.get(i).getName();
			Label tl = teamLabels[i];
			tl.setText(name);

			teamScoreLabels[i].setText("" + gs.points.get(gs.turnOrder.get(i)));

			Color col = gs.turnOrder.get(i).getColor().cpy();

			if (gs.currentTurnTeam != gs.turnOrder.get(i)){
				col.a = 0.5f;
			}

			teamLabels[i].setColor(col);
			teamScoreLabels[i].setColor(col);
		}

		if (gs.winner != null){
			winLabel.setText(gs.winner.getName() + " has won! Press R to restart.");
		} else {
			winLabel.setText("");
		}

		gs.hexStream().forEach(it -> {
			it.isOnCurrentPath = false;
			it.isOnFuturePath = false;
		});

		if (gs.selectedUnit != null) {
			moves = gs.selectedUnit.getMoves();
			if (gs.selectedUnit.getMaxMoves() == 0) moves = gs.selectedUnit.getAttacks();
			gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()).ifPresent(destHex -> {
				gs.selectedUnit.pathTo(destHex).forEach(it -> {
					if (moves-- > 0) {
						it.isOnCurrentPath = true;
					} else {
						it.isOnFuturePath = true;
					}
				});
			});
		}

		backgroundCloudStage.act();
		backgroundCloudStage.draw();

		batch.begin();
		gs.render(titleMove >= 0 && !resetInProgress);
		batch.end();

		infoLabel.setText("$" + gs.money.get(Team.Overminers));

		/*Optional<Hex> th = gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		th.ifPresent(it -> {
			if (it.unit != null) {
				infoLabel.setText(infoLabel.getText() + " HP: " + it.unit.getHealth() + "/" + it.unit.getMaxHealth() + " Moves: " + it.unit.getMoves() + "/" + it.unit.getMaxMoves() + " Attacks: " + it.unit.getAttacks());
			}
		});*/

		gs.particleStage.act();
		gs.particleStage.draw();

		uiStage.act();
		uiStage.draw();

		for (int i=0;i<4;++i){
			Color col = gs.turnOrder.get(i).getColor().cpy();
			col.a = 0.5f;

			if (gs.currentTurnTeam == gs.turnOrder.get(i)){
				batch.setTransformMatrix(new Matrix4(origMat));
				batch.begin();
				Sprite sp8 = getSprite("solid");
				sp8.setBounds(teamLabels[i].getX() + 123, teamLabels[i].getY() + teamLabels[i].getHeight() / 2, 300, teamLabels[i].getHeight());
				sp8.setColor(col);
				sp8.draw(batch);
				batch.end();
			}
		}

		if (gs.selectedUnitTypeToPlace != null){
			Util.showTutorialText("Good! Now click on a gray hex. Try to place the mining base away from our rivals' mining bases.");
		}

		if (gs.money.get(Team.Overminers) < 15 && gs.currentTurnTeam == Team.Overminers){
			Util.showTutorialText("K2", "Press Enter to end turn, or use the End Turn button.");
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (resetInProgress) return false;

		if (titleScreenUp){
			resetInProgress = true;
			return true;
		}

		if (keycode == Input.Keys.NUM_1) gs.selectedUnitTypeToPlace = Unit.UnitType.Mine;
		if (keycode == Input.Keys.NUM_2) gs.selectedUnitTypeToPlace = Unit.UnitType.Tank;
		if (keycode == Input.Keys.NUM_3) gs.selectedUnitTypeToPlace = Unit.UnitType.Scout;
		if (keycode == Input.Keys.NUM_4) gs.selectedUnitTypeToPlace = Unit.UnitType.Turret;
		if (keycode == Input.Keys.NUM_5) gs.selectedUnitTypeToPlace = Unit.UnitType.Block;

		if (keycode == Input.Keys.R){
			resetInProgress = true;
		}

		if (gs.selectedUnitTypeToPlace != null && Unit.factory(gs.selectedUnitTypeToPlace).getCost() > gs.money.get(Team.Overminers)){
			gs.selectedUnitTypeToPlace = null;
		}

		if (keycode == Input.Keys.K) gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()).ifPresent(it -> it.ttl = 1000);

		if (keycode == Input.Keys.ENTER) gs.endTurn();

		return false;
	}

	private void startOrRestart() {
		titleScreenUp = false;
		titleMove = -800;
		resetInProgress = false;
		if (gs != null) gs.dispose();

		gs = new GameState();
		gs.generate();
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (resetInProgress) return false;

		if (titleScreenUp){
			resetInProgress = true;
			return true;
		}

		Optional<Hex> th = gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

		th.ifPresent(it -> {
			if (button == Input.Buttons.LEFT) {
				gs.selectedUnit = null;

				if (gs.selectedUnitTypeToPlace != null) {
					if (Team.Overminers.dropUnit(it, gs.selectedUnitTypeToPlace)) {
						gs.selectedUnitTypeToPlace = null;
					}
				} else {
					if (it.unit != null && it.unit.getTeam() == Team.Overminers && it.unit.canBeSelected()) {
						gs.selectedUnit = it.unit;
					}
				}
			}

			if (button == Input.Buttons.RIGHT) {
				if (gs.selectedUnit != null && !gs.selectedUnit.isAnimating()){
					gs.selectedUnit.setCurrentDestination(it);
					gs.selectedUnit.executeMoves();
					gs.selectedUnit = null;
				}
			}
		});
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
