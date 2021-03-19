package physics;

import entities.Entity;
import entities.Player;
import lwjglUtil.vector.Vector3f;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import renderEngine.Loader;
import renderEngine.ModelData;
import renderEngine.OBJFileLoader;
import textures.ModelTexture;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionEngine {

    private static final float MAX_RANGE = 15.0f;

    private Map<Entity, BoundingBox> closeBoxes = new HashMap<Entity, BoundingBox>();
    private Player playerEntity;
    private BoundingBox playerBox;
    private float deltaTime;

    public CollisionEngine() {
    }

    public List<Entity> run(Player player, List<Entity> entities, Loader loader, float deltaTime) {
        closeBoxes.clear();
        this.deltaTime = deltaTime;

        playerEntity = player;
        //playerBox = new BoundingBox(player.getModel().getVertexPositions());
        //playerBox.calculate();

        List<Entity> closeEntities = new ArrayList<Entity>();

        for (Entity aEntity : entities) {
            if (isNear(player, aEntity)) {
                closeEntities.add(aEntity);
            }
        }

        List<Entity> boxEntities = new ArrayList<Entity>();

        for (Entity entity : closeEntities) {
            boxEntities.add(generateBoundingBox(entity, loader));

            checkCollision();
        }

        boxEntities.add(generatePlayerBoundingBox(playerEntity, loader));

        return boxEntities;
    }

    private boolean isNear(Player player, Entity aEntity) {
        float deltaX = player.getPosition().x - aEntity.getPosition().x;
        float deltaY = player.getPosition().y - aEntity.getPosition().y;
        float deltaZ = player.getPosition().z - aEntity.getPosition().z;

        float distance = (float) Math.sqrt((float) Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));

        if (distance <= MAX_RANGE) {
            return true;
        }

        return false;
    }

    private Entity generateBoundingBox(Entity entity, Loader loader) {
        BoundingBox box = new BoundingBox(entity.getModel().getRawModel().getVertexPositions());

        closeBoxes.put(entity, box);

        ModelData boxModel = box.calculate();
        RawModel boxRawModel = loader.loadToVAO(boxModel.getVertices(), boxModel.getTextureCoords(), boxModel.getNormals(), boxModel.getIndices());
        TexturedModel boxTexModel = new TexturedModel(boxRawModel, new ModelTexture(loader.loadTexture("white")));

        return new Entity(boxTexModel, new Vector3f(entity.getPosition().x, entity.getPosition().y + box.getCenter().y / 4, entity.getPosition().z), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
    }

    private Entity generatePlayerBoundingBox(Player entity, Loader loader) {
        //BoundingBox box = new BoundingBox(entity.getModel().getVertexPositions());
        BoundingBox box = new BoundingBox(null);

        ModelData boxModel = box.calculate();
        RawModel boxRawModel = loader.loadToVAO(boxModel.getVertices(), boxModel.getTextureCoords(), boxModel.getNormals(), boxModel.getIndices());
        TexturedModel boxTexModel = new TexturedModel(boxRawModel, new ModelTexture(loader.loadTexture("white")));

        return new Entity(boxTexModel, new Vector3f(entity.getPosition().x, entity.getPosition().y + box.getCenter().y / 4, entity.getPosition().z), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
    }

    private void checkCollision() {
        for (Entity closeBox : closeBoxes.keySet()) {
            float playerXOffset = playerEntity.getPosition().x;
            float playerYOffset = playerEntity.getPosition().y;
            float playerZOffset = playerEntity.getPosition().z;

            float closeXOffset = closeBox.getPosition().x;
            float closeYOffset = closeBox.getPosition().y;
            float closeZOffset = closeBox.getPosition().z;

            if ((playerBox.getxMin() + playerXOffset <= closeBoxes.get(closeBox).getxMax() + closeXOffset && playerBox.getxMax() + playerXOffset >= closeBoxes.get(closeBox).getxMin() + closeXOffset) &&
                    (playerBox.getyMin() + playerYOffset <= closeBoxes.get(closeBox).getyMax() + closeYOffset && playerBox.getyMax() + playerYOffset >= closeBoxes.get(closeBox).getyMin() + closeYOffset) &&
                    (playerBox.getzMin() + playerZOffset <= closeBoxes.get(closeBox).getzMax() + closeZOffset && playerBox.getzMax() + playerZOffset >= closeBoxes.get(closeBox).getzMin() + closeZOffset)) {
                Player player = (Player) playerEntity;
                player.unwindMove(deltaTime);
            }
        }
    }
}
