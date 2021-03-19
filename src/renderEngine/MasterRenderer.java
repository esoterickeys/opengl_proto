package renderEngine;

import anime.Animation.renderer.AnimatedModelRenderer;
import anime.Animation.renderer.AnimatedModelShader;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import lwjglUtil.vector.Matrix4f;
import lwjglUtil.vector.Vector4f;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrain.Terrain;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {

    public static final float fov = 70;
    public static final float nearPlane = 0.25f;
    public static final float farPlane = 1000;

    public static final float RED = 0.5f;
    public static final float GREEN = 0.5f;
    public static final float BLUE = 0.5f;

    private int WIDTH = 1280;
    private int HEIGHT = 720;

    private Matrix4f projectionMatrix;

    private StaticShader shader;
    private TerrainShader terrainShader = new TerrainShader();
    private AnimatedModelShader animateShader = new AnimatedModelShader();

    private EntityRenderer entityRenderer;
    private AnimatedModelRenderer animateRenderer;
    private TerrainRenderer terrainRenderer;
    private NormalMappingRenderer normalMapRenderer;
    private SkyboxRenderer skyboxRenderer;
    private ShadowMapMasterRenderer shadowMapRenderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    private Player player;


    public MasterRenderer(int width, int height, Loader loader, Camera camera) {
        createProjectionMatrix();
        enableCulling();

        this.shader = new StaticShader();
        this.animateRenderer = new AnimatedModelRenderer(projectionMatrix);
        this.entityRenderer = new EntityRenderer(shader, projectionMatrix);
        this.terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        this.skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
        this.shadowMapRenderer = new ShadowMapMasterRenderer(camera);

        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public static void enableCulling() {
        GL11.glEnable(GL_CULL_FACE);
        GL11.glCullFace(GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL_CULL_FACE);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void renderScene(List<Entity> entities, Player player, List<Entity> normalEntities, List<Terrain> terrains, List<Light> lights, Camera camera, float deltaTime, Vector4f clipPlane) {
        for (Terrain terrain : terrains) {
            processTerrain(terrain);
        }

        for (Entity entity : entities) {
            processEntity(entity);
        }

        for (Entity normalEntity : normalEntities) {
            processNormalMapEntity(normalEntity);
        }

        this.player = player;

        render(lights, camera, deltaTime, clipPlane);
    }

    public void render(List<Light> lights, Camera camera, float deltaTime, Vector4f clipPlane) {
        prepare();

        shader.start();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
        shader.stop();

        normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
        animateRenderer.render(player, camera);

        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();

        skyboxRenderer.render(camera, RED, GREEN, BLUE, deltaTime);

        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }

    public void prepare() {
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL_TEXTURE_2D, getShadowMapTexture());
    }

    private void createProjectionMatrix() {
        projectionMatrix = new Matrix4f();

        float aspectRatio = (float) WIDTH / (float) HEIGHT;
        float yScale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))));
        float xScale = yScale / aspectRatio;
        float frustrumLength = farPlane - nearPlane;


        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((farPlane + nearPlane) / frustrumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustrumLength);
        projectionMatrix.m33 = 0;
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);

        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processNormalMapEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);

        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void renderShadowMap(List<Entity> entityList, Light sun) {
        for (Entity entity : entityList) {
            processEntity(entity);
        }

        shadowMapRenderer.render(entities, sun);
        entities.clear();
    }

    public int getShadowMapTexture() {
        return shadowMapRenderer.getShadowMap();
    }

    public void cleanUp() {
        shader.cleanUp();
        animateShader.cleanUp();
        terrainShader.cleanUp();
        skyboxRenderer.getShader().cleanUp();
        normalMapRenderer.cleanUp();
        shadowMapRenderer.cleanUp();
    }
}
