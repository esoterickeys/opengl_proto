package anime.Engine.skybox;

import anime.Engine.shaders.ShaderProgram;
import anime.Engine.utils.MyFile;
import anime.Engine.shaders.UniformMatrix;

public class SkyboxShader extends ShaderProgram {

	private static final MyFile VERTEX_SHADER = new MyFile("src/anime/Engine/skybox/skyboxVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("src/anime/Engine/skybox/skyboxFragment.glsl");

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");

	public SkyboxShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(projectionViewMatrix);
	}
}
