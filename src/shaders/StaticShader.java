package shaders;

import entities.Camera;
import entities.Light;
import lwjglUtil.vector.Matrix4f;
import lwjglUtil.vector.Vector2f;
import lwjglUtil.vector.Vector3f;
import lwjglUtil.vector.Vector4f;
import toolbox.Maths;

import java.util.List;

public class StaticShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

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
    private int locationUseFakeLighting;
    private int locationSkyColour;
    private int locationNumberOfRows;
    private int locationOffset;
    private int locationPlane;
    private int locationToShadowMapSpace;
    private int locationShadowMap;
    private int locationSpecularMap;
    private int locationUsesSpecularMap;
    private int locationModelTexture;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindFragOutput(0, "out_Color");
        super.bindFragOutput(1, "out_BrightColor");
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
        locationUseFakeLighting = super.getUniformLocation("useFakeLighting");
        locationSkyColour = super.getUniformLocation("skyColour");
        locationNumberOfRows = super.getUniformLocation("numberOfRows");
        locationOffset = super.getUniformLocation("offset");
        locationPlane = super.getUniformLocation("plane");
        locationToShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        locationShadowMap = super.getUniformLocation("shadowMap");
        locationSpecularMap = super.getUniformLocation("specularMap");
        locationUsesSpecularMap = super.getUniformLocation("usesSpecularMap");
        locationModelTexture = super.getUniformLocation("modelTexture");

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

    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix(locationToShadowMapSpace, matrix);
    }

    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(locationNumberOfRows, numberOfRows);
    }

    public void loadOffset(float x, float y) {
        super.load2DVector(locationOffset, new Vector2f(x, y));
    }

    public void loadSkyColour(float r, float g, float b) {
        super.loadVector(locationSkyColour, new Vector3f(r, g, b));
    }

    public void loadFakeLightingVariable(boolean useFake) {
        super.loadBoolean(locationUseFakeLighting, useFake);
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

    public void connectTextureUnits() {
        super.loadInt(locationModelTexture, 0);
        super.loadInt(locationSpecularMap, 1);
        super.loadInt(locationShadowMap, 5);
    }

    public void loadUseSpecularMap(boolean useMap) {
        super.loadBoolean(locationUsesSpecularMap, useMap);
    }
}
