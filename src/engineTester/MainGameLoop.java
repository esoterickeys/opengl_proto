package engineTester;

import anime.Animation.animatedModel.AnimatedModel;
import anime.Animation.animation.Animation;
import anime.Animation.loaders.AnimatedModelLoader;
import anime.Animation.loaders.AnimationLoader;
import anime.Engine.utils.MyFile;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import lwjglUtil.vector.Vector2f;
import lwjglUtil.vector.Vector3f;
import lwjglUtil.vector.Vector4f;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import physics.CollisionEngine;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.*;
import models.RawModel;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameLoop {
    public static long window;

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        window = DisplayManager.getWindow();

        Loader loader = new Loader();
        AnimatedModelLoader animatedLoader = new AnimatedModelLoader();
        List<AnimatedModel> animatedModels = new ArrayList<AnimatedModel>();

        TextMaster.init(loader);

        FontType font = new FontType(loader.loadFontTexture("candara"), new File("res/candara.fnt"));
        //GUIText text = new GUIText("TEST", 8, font, new Vector2f(0.75f, 0.1f), 0.25f, true);
        //text.setColour(.5f, .5f, 0);

        AnimatedModel playerModel = animatedLoader.loadEntity(new MyFile("res/model.dae"),
                new MyFile("res/diffuse.png"));

        Animation playerRun = AnimationLoader.loadAnimation(new MyFile("res/model.dae"));
        Player player = new Player(playerModel, playerRun, window);
        player.setPosition(new Vector3f(3, 5, -100));
        player.setRotX(0);
        player.setRotY(0);
        player.setRotZ(0);
        player.setScale(0.5f);
        animatedModels.add(player);

        Camera camera = new Camera(window, player);


        MasterRenderer renderer = new MasterRenderer(DisplayManager.getWidth(), DisplayManager.getHeight(), loader, camera);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        ModelData stallData = OBJFileLoader.loadOBJ("stall");
        RawModel stallRawModel = loader.loadToVAO(stallData.getVertices(), stallData.getTextureCoords(), stallData.getNormals(), stallData.getIndices());
        TexturedModel stallTexModel = new TexturedModel(stallRawModel, new ModelTexture(loader.loadTexture("stallTexture")));
        ModelTexture stallTexture = stallTexModel.getTexture();
        stallTexture.setShineDamper(10);
        stallTexture.setReflectivity(1);

        ModelData lampData = OBJFileLoader.loadOBJ("lantern");
        RawModel lampRawModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices());
        TexturedModel lampTexModel = new TexturedModel(lampRawModel, new ModelTexture(loader.loadTexture("lantern")));
        ModelTexture lampTexture = lampTexModel.getTexture();
        lampTexture.setShineDamper(10);
        lampTexture.setReflectivity(1);
        lampTexture.setSpecularMap(loader.loadTexture("lanternS"));


        ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
        RawModel grassRawModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(), grassData.getIndices());
        TexturedModel grassTexModel = new TexturedModel(grassRawModel, new ModelTexture(loader.loadTexture("grassTexture")));
        grassTexModel.getTexture().setHasTransparency(true);
        grassTexModel.getTexture().setUseFakeLighting(true);
        ModelTexture grassTexture = grassTexModel.getTexture();
        grassTexture.setShineDamper(10);
        grassTexture.setReflectivity(.5f);

        ModelData flowerData = OBJFileLoader.loadOBJ("grassModel");
        RawModel flowerRawModel = loader.loadToVAO(flowerData.getVertices(), flowerData.getTextureCoords(), flowerData.getNormals(), flowerData.getIndices());
        TexturedModel flowerTexModel = new TexturedModel(flowerRawModel, new ModelTexture(loader.loadTexture("flower")));
        flowerTexModel.getTexture().setHasTransparency(true);
        flowerTexModel.getTexture().setUseFakeLighting(true);
        ModelTexture flowerTexture = flowerTexModel.getTexture();
        flowerTexture.setShineDamper(10);
        grassTexture.setReflectivity(.5f);

        ModelData treeData = OBJFileLoader.loadOBJ("lowPolyTree");
        RawModel treeRawModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
        TexturedModel treeTexModel = new TexturedModel(treeRawModel, new ModelTexture(loader.loadTexture("lowPolyTree")));
        ModelTexture treeTexture = treeTexModel.getTexture();
        treeTexture.setShineDamper(10);
        treeTexture.setReflectivity(1);

        ModelData fernData = OBJFileLoader.loadOBJ("fern");
        RawModel fernRawModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fernAtlas"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fernTexModel = new TexturedModel(fernRawModel, fernTextureAtlas);
        ModelTexture fernTexture = fernTexModel.getTexture();
        fernTexModel.getTexture().setHasTransparency(true);
        fernTexture.setShineDamper(10);
        fernTexture.setReflectivity(1);

        ModelData cherryData = OBJFileLoader.loadOBJ("cherry");
        RawModel cherryRawModel = loader.loadToVAO(cherryData.getVertices(), cherryData.getTextureCoords(), cherryData.getNormals(), cherryData.getIndices());
        ModelTexture cherryModelTexture = new ModelTexture(loader.loadTexture("cherry"));
        TexturedModel cherryTexModel = new TexturedModel(cherryRawModel, cherryModelTexture);
        ModelTexture cherryTexture = cherryTexModel.getTexture();
        cherryTexModel.getTexture().setHasTransparency(true);
        cherryTexture.setShineDamper(10);
        cherryTexture.setReflectivity(1);
        cherryTexture.setSpecularMap(loader.loadTexture("cherryS"));

        List<Terrain> terrains = new ArrayList<Terrain>();
        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");
        terrains.add(terrain);

        List<Entity> entities = new ArrayList<Entity>();
        Random rnd = new Random();
        for (int i = 0; i < 100; i++) {
            if (i % 20 == 0) {
                float x = rnd.nextFloat() * 800;
                float z = rnd.nextFloat() * -800;
                float y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(grassTexModel, new Vector3f(x, y, z), 0, 0, 0, 1.8f));

                x = rnd.nextFloat() * 800;
                z = rnd.nextFloat() * -800;
                y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(flowerTexModel, new Vector3f(x, y, z), 0, 0, 0, 2.3f));
            }

            if (i % 5 == 0) {
                float x = rnd.nextFloat() * 800;
                float z = rnd.nextFloat() * -800;
                float y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(fernTexModel, rnd.nextInt(4), new Vector3f(x, y, z), 0, 0, 0, 0.9f));

                x = rnd.nextFloat() * 800;
                z = rnd.nextFloat() * -800;
                y = terrain.getHeightOfTerrain(x, z) - 3;
                entities.add(new Entity(treeTexModel, new Vector3f(x, y, z), 0, 0, 0, rnd.nextFloat() * 1 + 1));

                x = rnd.nextFloat() * 800;
                z = rnd.nextFloat() * -800;
                y = terrain.getHeightOfTerrain(x, z) - 3;
                entities.add(new Entity(cherryTexModel, new Vector3f(x, y, z), 0, 0, 0, rnd.nextFloat() * 1 + 5));
            }
        }

        List<Entity> normalEntities = new ArrayList<Entity>();
        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
        barrelModel.getTexture().setSpecularMap(loader.loadTexture("barrelS"));

        normalEntities.add(new Entity(barrelModel, new Vector3f(185, 10, -285), 0, 0, 0, 1f));

        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);

        Light spotlight = new Light(new Vector3f(player.getPosition().x, player.getPosition().y + 5, player.getPosition().z),
                new Vector3f(1, 1, 1),
                new Vector3f(.00095f, .00095f, .00075f),
                19.5f,
                new Vector3f(camera.getFront().x, camera.getFront().y, camera.getFront().z));

        //lights.add(spotlight);

        Light pickableLight = new Light(new Vector3f(165, terrain.getHeightOfTerrain(165, -293) + 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f));
        lights.add(pickableLight);
        //lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
        //lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));

        Entity pickableLamp = new Entity(lampTexModel, new Vector3f(165, terrain.getHeightOfTerrain(165, -293), -293), 0, 0, 0, 1);
        entities.add(pickableLamp);
        entities.add(new Entity(lampTexModel, new Vector3f(345, 10, -300), 0, 0, 0, 1));
        entities.add(new Entity(lampTexModel, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1));

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-.8f, .9f), new Vector2f(0.2f, 0.3f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<WaterTile>();
        WaterTile water = new WaterTile(0, 0, -10);
        waters.add(water);

        GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        GuiTexture refraction = new GuiTexture(buffers.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        //guis.add(reflection);
        //guis.add(refraction);

        GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        //guis.add(shadowMap);

        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);

        ParticleSystem pSystem = new ParticleSystem(particleTexture, 500, 10, 0.1f, 1, 1.6f);
        pSystem.randomizeRotation();
        pSystem.setLifeError(0.1f);
        pSystem.setSpeedError(0.4f);
        pSystem.setScaleError(0.8f);

        Fbo multiSampleFbo = new Fbo(DisplayManager.getWidth(), DisplayManager.getHeight());
        Fbo outputFbo = new Fbo(DisplayManager.getWidth(), DisplayManager.getHeight(), Fbo.DEPTH_TEXTURE);
        Fbo outputFbo2 = new Fbo(DisplayManager.getWidth(), DisplayManager.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init(loader);

        double initialTime = System.currentTimeMillis();

        double fpsLimit = 1.0 / 60.0;
        double lastUpdateTime = initialTime;
        double lastFrameTime = initialTime;
        double now;
        float deltaTime;

        CollisionEngine collisionEngine = new CollisionEngine();

        while (!glfwWindowShouldClose(window)) {
            now = System.currentTimeMillis();
            deltaTime = (float) (now - lastUpdateTime) / 1000f;
            DisplayManager.setDeltaTime(deltaTime);

            player.move(terrain, deltaTime);
            player.update();

            camera.input();
            camera.move();

            spotlight.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 5, player.getPosition().z));
            spotlight.setConeDirection(new Vector3f(camera.getFront().x, camera.getFront().y, camera.getFront().z));

            //List<Entity> collisionEntities = collisionEngine.run(player, entities, loader, deltaTime);

            /**
             picker.update();
             Vector3f terrainPoint = picker.getCurrentTerrainPoint();
             if (terrainPoint != null) {
             pickableLamp.setPosition(terrainPoint);
             pickableLight.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
             }
             **/

            if ((now - lastFrameTime) >= fpsLimit) {
                pSystem.generateParticles(pickableLight.getPosition());

                ParticleMaster.update(camera);

                renderer.renderShadowMap(entities, sun);

                GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

                buffers.bindReflectionFrameBuffer();
                float distance = 2 * (camera.getPosition().y - water.getHeight());
                camera.getPosition().y -= distance;
                camera.invertPitch();
                renderer.renderScene(entities, player, normalEntities, terrains, lights, camera, deltaTime, new Vector4f(0, 1, 0, -water.getHeight()));
                camera.getPosition().y += distance;
                camera.invertPitch();

                buffers.bindRefractionFrameBuffer();
                renderer.renderScene(entities, player, normalEntities, terrains, lights, camera, deltaTime, new Vector4f(0, -1, 0, water.getHeight() + 2));

                GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
                buffers.unbindCurrentFrameBuffer();

                multiSampleFbo.bindFrameBuffer();

                //entities.addAll(collisionEntities);
                renderer.renderScene(entities, player, normalEntities, terrains, lights, camera, deltaTime, new Vector4f(0, 0, 0, 0));
                //entities.removeAll(collisionEntities);

                waterRenderer.render(waters, camera, deltaTime, sun);
                ParticleMaster.renderParticles(camera);
                multiSampleFbo.unbindFrameBuffer();
                multiSampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
                multiSampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);

                PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());

                guiRenderer.render(guis);

                TextMaster.render();

                DisplayManager.updateDisplay();

                lastFrameTime = now;
            }

            lastUpdateTime = now;
        }

        PostProcessing.cleanUp();
        multiSampleFbo.cleanUp();
        outputFbo.cleanUp();
        outputFbo2.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        buffers.cleanUp();
        guiRenderer.cleanUp();
        waterShader.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
