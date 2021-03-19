package shaders;

import entities.Camera;
import entities.Light;
import lwjglUtil.vector.Matrix4f;
import lwjglUtil.vector.Vector3f;
import lwjglUtil.vector.Vector4f;
import toolbox.Maths;

import java.util.List;

public class TerrainShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.txt";

    private int locationTransformationMatrix;
    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationLightPosition[];
    private int locationLightColour[];
    private int locationAttenuation[];
    private int locationConeAngle[];
    private int locationConeDirection[];
    private int locationShineDamper;
    private int locationReflectivity;
    private int locationSkyColour;
    private int locationBackgroundTexture;
    private int locationrTexture;
    private int locationgTexture;
    private int locationbTexture;
    private int locationBlendMap;
    private int locationPlane;
    private int locationToShadowMapSpace;
    private int locationShadowMap;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationShineDamper = super.getUniformLocation("shineDamper");
        locationReflectivity = super.getUniformLocation("reflectivity");
        locationSkyColour = super.getUniformLocation("skyColour");
        locationBackgroundTexture = super.getUniformLocation("backgroundTexture");
        locationrTexture = super.getUniformLocation("rTexture");
        locationgTexture = super.getUniformLocation("gTexture");
        locationbTexture = super.getUniformLocation("bTexture");
        locationBlendMap = super.getUniformLocation("blendMap");
        locationPlane = super.getUniformLocation("plane");
        locationToShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        locationShadowMap = super.getUniformLocation("shadowMap");

        locationLightPosition = new int[MAX_LIGHTS];
        locationLightColour = new int[MAX_LIGHTS];
        locationAttenuation = new int[MAX_LIGHTS];
        locationConeAngle = new int[MAX_LIGHTS];
        locationConeDirection = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++) {
            locationLightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            locationLightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
            locationConeAngle[i] = super.getUniformLocation("coneAngle[" + i + "]");
            locationConeDirection[i] = super.getUniformLocation("coneDirection[" + i + "]");
        }
    }

    public void loadClipPlane(Vector4f plane) {
        super.loadVector(locationPlane, plane);
    }

    public void connectTextureUnits() {
        super.loadInt(locationBackgroundTexture, 0);
        super.loadInt(locationrTexture, 1);
        super.loadInt(locationgTexture, 2);
        super.loadInt(locationbTexture, 3);
        super.loadInt(locationBlendMap, 4);
        super.loadInt(locationShadowMap, 5);
    }

    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix(locationToShadowMapSpace, matrix);
    }

    public void loadSkyColour(float r, float g, float b) {
        super.loadVector(locationSkyColour, new Vector3f(r, g, b));
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(locationViewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }

    public void loadLights(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                super.loadVector(locationLightPosition[i], lights.get(i).getPosition());
                super.loadVector(locationLightColour[i], lights.get(i).getColour());
                super.loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
                super.loadFloat(locationConeAngle[i], lights.get(i).getConeAngle());
                super.loadVector(locationConeDirection[i], lights.get(i).getConeDirection());
            } else {
                super.loadVector(locationLightPosition[i], new Vector3f(0, 0, 0));
                super.loadVector(locationLightColour[i], new Vector3f(0, 0, 0));
                super.loadVector(locationAttenuation[i], new Vector3f(1, 0, 0));
                super.loadFloat(locationConeAngle[i], 0f);
                super.loadVector(locationConeDirection[i], new Vector3f(0, 0, 0));
            }
        }
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(locationShineDamper, damper);
        super.loadFloat(locationReflectivity, reflectivity);
    }
}
