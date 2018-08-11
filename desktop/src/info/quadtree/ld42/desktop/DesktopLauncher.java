package info.quadtree.ld42.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import info.quadtree.ld42.LD42;

public class DesktopLauncher {
	public static void main (String[] arg) {
		TexturePacker.processIfModified("../../raw_assets", ".", "main");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new LD42(), config);
	}
}
