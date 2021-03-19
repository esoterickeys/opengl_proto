package entities;

import anime.Animation.animatedModel.AnimatedModel;
import anime.Animation.animation.Animation;
import terrain.Terrain;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends AnimatedModel {

    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    public static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;

    Animation currentAnimation = null;
    private Animation runAnimation;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardSpeed = 0;

    private boolean isInAir = false;

    private long windowId;

    /**
     * public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, long windowId) {
     * super(model, position, rotX, rotY, rotZ, scale);
     * this.windowId = windowId;
     * }
     **/

    public Player(AnimatedModel model, Animation run, long windowId) {
        super(model.getModel(), model.getTexture(), model.getRootJoint(), model.getJointCount(), model.getPosition(), model.getRotX(), model.getRotY(), model.getRotX(), model.getScale());
        this.runAnimation = run;
        this.windowId = windowId;
    }

    public void move(Terrain terrain, float deltaTime) {
        checkInputs();

        super.increaseRotation(0, currentTurnSpeed * deltaTime, 0);
        float distance = currentSpeed * deltaTime;

        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));

        super.increasePosition(dx, 0, dz);
        upwardSpeed += GRAVITY * deltaTime;
        super.increasePosition(0, upwardSpeed * deltaTime, 0);

        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);

        if (super.getPosition().y < terrainHeight) {
            upwardSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
    }

    public void unwindMove(float deltaTime) {
        float distance = currentSpeed * deltaTime;

        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));

        super.increasePosition(-dx, 0, -dz);
    }

    private void jump() {
        if (!isInAir) {
            this.upwardSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void setAnimation(Animation animation) {
        if (currentAnimation != animation) {
            currentAnimation = animation;
            doAnimation(animation);
        }
    }

    private void checkInputs() {
        if (glfwGetKey(windowId, GLFW_KEY_W) == 1) {
            this.currentSpeed = RUN_SPEED;
            setAnimation(runAnimation);
        } else if (glfwGetKey(windowId, GLFW_KEY_S) == 1) {
            this.currentSpeed = -RUN_SPEED;
            setAnimation(runAnimation);
        } else {
            this.currentSpeed = 0;
            setAnimation(null);
        }

        if (glfwGetKey(windowId, GLFW_KEY_D) == 1) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (glfwGetKey(windowId, GLFW_KEY_A) == 1) {
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (glfwGetKey(windowId, GLFW_KEY_SPACE) == 1) {
            jump();
        }
    }

    public void doAnimation(Animation animation) {
        super.doAnimation(animation);
    }

    public void update() {
        super.update();
    }
}
