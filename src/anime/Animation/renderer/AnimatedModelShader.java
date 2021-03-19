package anime.Animation.renderer;

import anime.Engine.shaders.*;
import anime.Engine.utils.MyFile;
import entities.Camera;
import lwjglUtil.vector.Matrix4f;
import toolbox.Maths;

public class AnimatedModelShader extends ShaderProgram {

    private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
    private static final int DIFFUSE_TEX_UNIT = 0;

    private static final MyFile VERTEX_SHADER = new MyFile("src/anime/Animation/renderer/animatedEntityVertex.glsl");
    private static final MyFile FRAGMENT_SHADER = new MyFile("src/anime/Animation/renderer/animatedEntityFragment.glsl");

    protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
    protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
    protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
    private UniformSampler diffuseMap = new UniformSampler("diffuseMap");

    /**
     * Creates the shader program for the {@link AnimatedModelRenderer} by
     * loading up the vertex and fragment shader code files. It also gets the
     * location of all the specified uniform variables, and also indicates that
     * the diffuse texture will be sampled from texture unit 0.
     */
    public AnimatedModelShader() {
        super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords", "in_normal", "in_jointIndices",
                "in_weights");
        super.storeAllUniformLocations(projectionViewMatrix, transformationMatrix, projectionMatrix, viewMatrix, diffuseMap, lightDirection, jointTransforms);

        connectTextureUnits();
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        transformationMatrix.loadMatrix(matrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        projectionMatrix.loadMatrix(projection);
    }

    public void loadViewMatrix(Matrix4f view) {
        viewMatrix.loadMatrix(view);
    }

    /**
     * Indicates which texture unit the diffuse texture should be sampled from.
     */
    private void connectTextureUnits() {
        super.start();
        diffuseMap.loadTexUnit(DIFFUSE_TEX_UNIT);
        super.stop();
    }

}
