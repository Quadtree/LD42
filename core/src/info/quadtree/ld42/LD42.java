package info.quadtree.ld42;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
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

	GameState gs;

	Map<String, Sprite> assetMap = new HashMap<>();

	public Sprite getSprite(String name){
		if (!assetMap.containsKey(name)) assetMap.put(name, atlas.createSprite(name));

		return assetMap.get(name);
	}
	
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
		Gdx.gl.glClearColor(0.7f, 0.7f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		gs.render();
		batch.end();
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

		if (keycode == Input.Keys.K) gs.getHexAtScreenPos(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()).ifPresent(it -> it.ttl = 1000);

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
			if (gs.selectedUnitTypeToPlace != null){
				if (it.owner == Team.Nobody || it.owner == Team.Overminers) {
					Unit.factory(gs.selectedUnitTypeToPlace).setTeam(Team.Overminers).moveTo(it);
					gs.selectedUnitTypeToPlace = null;
					gs.recomputeOwnership();
				}
			} else {
				if (it.unit != null && it.unit.getTeam() == Team.Overminers){
					gs.selectedUnit = it.unit;
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
