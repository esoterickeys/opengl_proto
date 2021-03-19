package anime.Animation.renderer;

import anime.Engine.utils.OpenGlUtils;
import entities.Camera;
import lwjglUtil.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

import anime.Animation.animatedModel.AnimatedModel;
import renderEngine.DisplayManager;
import toolbox.Maths;

/**
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 *
 * @author Karl
 */
public class AnimatedModelRenderer {

    private AnimatedModelShader shader;

    /**
     * Initializes the shader program used for rendering animated models.
     */
    public AnimatedModelRenderer(Matrix4f projectionMatrix) {
        this.shader = new AnimatedModelShader();
        this.shader.loadProjectionMatrix(projectionMatrix);
    }

    /**
     * Renders an animated entity. The main thing to note here is that all the
     * joint transforms are loaded up to the shader to a uniform array. Also 5
     * attributes of the VAO are enabled before rendering, to include joint
     * indices and weights.
     *
     * @param entity - the animated entity to be rendered.
     * @param camera - the camera used to render the entity.
     */
    public void render(AnimatedModel entity, Camera camera) {
        prepare(entity, camera);
        entity.getTexture().bindToUnit(0);
        entity.getModel().bind(0, 1, 2, 3, 4);
        shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
        entity.getModel().unbind(0, 1, 2, 3, 4);
        finish();
    }

    /**
     * Deletes the shader program when the game closes.
     */
    public void cleanUp() {
        shader.cleanUp();
    }

    /**
     * Starts the shader program and loads up the projection view matrix, as
     * well as the light direction. Enables and disables a few settings which
     * should be pretty self-explanatory.
     *
     * @param camera - the camera being used.
     */
    private void prepare(AnimatedModel entity, Camera camera) {
        shader.start();

        Matrix4f projectionMatrix = camera.createProjectionMatrix();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectViewMatrix = Matrix4f.mul(projectionMatrix, viewMatrix, null);

        shader.projectionViewMatrix.loadMatrix(projectViewMatrix);

        Matrix4f view = Maths.createViewMatrix(camera);
        shader.loadViewMatrix(view);

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);

        //shader.lightDirection.loadVec3(lightDir);

        OpenGlUtils.antialias(true);
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
    }

    /**
     * Stops the shader program after rendering the entity.
     */
    private void finish() {
        shader.stop();
    }

}
