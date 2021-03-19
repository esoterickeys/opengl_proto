package particles;

import lwjglUtil.vector.Matrix4f;
import lwjglUtil.vector.Vector2f;
import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/particles/particleVShader.txt";
    private static final String FRAGMENT_FILE = "src/particles/particleFShader.txt";

    private int locationNumberOfRows;
    private int location_projectionMatrix;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationNumberOfRows = super.getUniformLocation("numberOfRows");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "modelViewMatrix");
        super.bindAttribute(5, "texOffsets");
        super.bindAttribute(6, "blendFactor");
    }

    protected void loadNumberOfRows(float numRows) {
        super.loadFloat(locationNumberOfRows, numRows);
    }

    protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(location_projectionMatrix, projectionMatrix);
    }

}
