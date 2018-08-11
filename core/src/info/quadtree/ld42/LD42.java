package info.quadtree.ld42;

import com.badlogic.gdx.*;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import info.quadtree.ld42.unit.Unit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

	Stage uiStage;

	Label infoLabel;
	
	@Override
	public void create () {
		LD42.s = this;
		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		gs = new GameState();

		gs.generate();

		InputMultiplexer mp = new InputMultiplexer();
		mp.addProcessor(this);

		Gdx.input.setInputProcessor(mp);

		defaultLabelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

		uiStage = new Stage();
		infoLabel = new Label("TEST", defaultLabelStyle);
		infoLabel.setPosition(20, Gdx.graphics.getHeight() - 40);
		uiStage.addActor(infoLabel);

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

	@Override
	public void render () {
		gs.hexStream().forEach(it -> {
			it.isOnCurrentPath = false;
			it.isOnFuturePath = false;
		});

		if (gs.selectedUnit != null) {
			gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()).ifPresent(destHex -> {
				GraphPath<Hex> hexPath = new DefaultGraphPath<>();
				gs.pathFinder.searchNodePath(gs.selectedUnit.getHex(), destHex, gs.defaultHeuristic, hexPath);

				//System.err.println("START");
				for (int i=0;i<hexPath.getCount();++i){
					hexPath.get(i).isOnCurrentPath = true;
					//System.err.println(hexPath.get(i).getX() + "," + hexPath.get(i).getY());
				}
			});
		}



		Gdx.gl.glClearColor(0.7f, 0.7f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		gs.render();
		batch.end();

		infoLabel.setText("Money: $" + gs.money.get(Team.Overminers) + "   Ore: " + gs.points.get(Team.Overminers) + "   Turn: " + gs.turnNum);

		uiStage.act();
		uiStage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.NUM_1) gs.selectedUnitTypeToPlace = Unit.UnitType.Mine;
		if (keycode == Input.Keys.NUM_2) gs.selectedUnitTypeToPlace = Unit.UnitType.Tank;
		if (keycode == Input.Keys.NUM_3) gs.selectedUnitTypeToPlace = Unit.UnitType.Scout;
		if (keycode == Input.Keys.NUM_4) gs.selectedUnitTypeToPlace = Unit.UnitType.Turret;
		if (keycode == Input.Keys.NUM_5) gs.selectedUnitTypeToPlace = Unit.UnitType.Block;

		if (gs.selectedUnitTypeToPlace != null && Unit.factory(gs.selectedUnitTypeToPlace).getCost() > gs.money.get(Team.Overminers)){
			gs.selectedUnitTypeToPlace = null;
		}

		if (keycode == Input.Keys.K) gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()).ifPresent(it -> it.ttl = 1000);

		if (keycode == Input.Keys.ENTER) gs.endTurn();

		return false;
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

		Optional<Hex> th = gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

		th.ifPresent(it -> {
			if (button == Input.Buttons.LEFT) {
				gs.selectedUnit = null;

				if (gs.selectedUnitTypeToPlace != null) {
					if (it.owner == Team.Nobody || it.owner == Team.Overminers) {
						Unit u = Unit.factory(gs.selectedUnitTypeToPlace);
						u.startFall();
						gs.money.put(Team.Overminers, gs.money.get(Team.Overminers) - u.getCost());
						u.setTeam(Team.Overminers).moveTo(it);
						gs.selectedUnitTypeToPlace = null;
						gs.recomputeOwnership();
					}
				} else {
					if (it.unit != null && it.unit.getTeam() == Team.Overminers && it.unit.canBeSelected()) {
						gs.selectedUnit = it.unit;
					}
				}
			}

			if (button == Input.Buttons.RIGHT) {
				if (gs.selectedUnit != null){
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
