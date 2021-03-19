package anime.src.main;

import anime.Animation.animatedModel.AnimatedModel;
import anime.Animation.animation.Animation;
import anime.Animation.loaders.AnimatedModelLoader;
import anime.Animation.loaders.AnimationLoader;
import anime.Engine.scene.ICamera;
import anime.Engine.scene.Scene;
import anime.Engine.utils.MyFile;

public class SceneLoader {

	/**
	 * Sets up the scene. Loads the entity, load the animation, tells the entity
	 * to do the animation, sets the light direction, creates the camera, etc...
	 *
	 *            - the folder containing all the information about the animated entity
	 *            (mesh, animation, and texture info).
	 * @return The entire scene.
	 */
	public static Scene loadScene() {
		ICamera camera = new Camera();
		AnimatedModel entity = AnimatedModelLoader.loadEntity(new MyFile("res/model.dae"),
				new MyFile("res/diffuse.png"));
		Animation animation = AnimationLoader.loadAnimation(new MyFile("res/model.dae"));
		entity.doAnimation(animation);
		Scene scene = new Scene(entity, camera);
		scene.setLightDirection(GeneralSettings.LIGHT_DIR);
		return scene;
	}

}
