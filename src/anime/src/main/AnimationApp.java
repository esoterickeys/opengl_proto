package anime.src.main;

import anime.Engine.renderEngine.RenderEngine;
import anime.Engine.scene.Scene;
import anime.Engine.utils.MyFile;
import renderEngine.DisplayManager;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class AnimationApp {

	/**
	 * Initialises the engine and loads the scene. For every frame it updates the
	 * camera, updates the animated entity (which updates the animation),
	 * renders the scene to the screen, and then updates the display. When the
	 * display is close the engine gets cleaned up.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		RenderEngine engine = RenderEngine.init();

		Scene scene = SceneLoader.loadScene();

		double initialTime = System.currentTimeMillis();

		double fpsLimit = 1.0 / 60.0;
		double lastUpdateTime = initialTime;
		double lastFrameTime = initialTime;
		double now;
		float deltaTime;

		while (!glfwWindowShouldClose(DisplayManager.getWindow())) {
			now = System.currentTimeMillis();
			deltaTime = (float) (now - lastUpdateTime) / 1000f;
			DisplayManager.setDeltaTime(deltaTime);

			scene.getCamera().input();
			scene.getCamera().move();
			scene.getAnimatedModel().update();
			engine.renderScene(scene);
			engine.update();

			lastFrameTime = now;
			lastUpdateTime = now;
		}

		engine.close();

	}

}
