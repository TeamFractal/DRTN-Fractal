

// For pre-compiled version, please see:
// https://github.com/TeamFractal/DRTN-Fractal/releases/download/v1.0.1/desktop-1.0.1.jar

package io.github.teamfractal.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.teamfractal.RoboticonQuest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Duck-Related Roboticon Quest";
		config.addIcon("icon.png", Files.FileType.Internal);
		config.backgroundFPS = 1;
		config.vSyncEnabled = true;
		config.width = 1024;
		config.height = 512;
		config.resizable = false;

		new LwjglApplication(new RoboticonQuest(), config);
	}
}
