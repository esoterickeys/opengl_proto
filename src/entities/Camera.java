package entities;

import lwjglUtil.vector.Matrix4f;
import lwjglUtil.vector.Vector2f;
import lwjglUtil.vector.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWScrollCallback;
import renderEngine.DisplayManager;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.25f;
    public static final float FAR_PLANE = 1000;

    private Matrix4f viewMatrix = new Matrix4f();

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector2f currentMouseWheel = new Vector2f(0, 0);

    private Vector2f currentMousePos = new Vector2f(0, 0);
    private Vector2f deltaMousePos = new Vector2f(0, 0);

    private DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
    private DoubleBuffer mouseY = BufferUtils.createDoubleBuffer(1);

    private GLFWScrollCallback scrollCallback;

    private int WIDTH = 1280;
    private int HEIGHT = 720;

    private int mouseCenterX = WIDTH / 2;
    private int mouseCenterY = HEIGHT / 2;

    private int newMouseX = -1;
    private int newMouseY = -1;

    private int mouseDx = -1;
    private int mouseDy = -1;

    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f front = new Vector3f(0, 0, 0);
    private float pitch = 20;
    private float yaw = 0;
    private float roll = 0;

    private Player player;

    private long windowId;

    public Camera(long windowId, Player player) {
        this.windowId = windowId;
        this.player = player;

        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                currentMouseWheel.x = (float) xoffset;
                currentMouseWheel.y = (float) yoffset;
            }
        };
    }

    public void input() {
        glfwSetScrollCallback(windowId, scrollCallback);

        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

        glfwGetCursorPos(windowId, mouseX, mouseY);
        mouseX.rewind();
        mouseY.rewind();

        mouseDx = newMouseX - (int) mouseX.get(0);
        mouseDy = newMouseY - (int) mouseY.get(0);

        newMouseX = (int) mouseX.get(0);
        newMouseY = (int) mouseY.get(0);
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();

        calculateCameraPosition(horizontalDistance, verticalDistance);

        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);

        calculateFront();

        currentMouseWheel.x = 0;
        currentMouseWheel.y = 0;
    }

    public void invertPitch() {
        pitch = -pitch;

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();

        calculateCameraPosition(horizontalDistance, verticalDistance);
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));

        if (player.getPosition().x <= (offsetX + 5) && player.getPosition().z >= (offsetZ) + 5) {
            position.x = 5;
            position.z = 5;
        } else {
            position.x = player.getPosition().x - offsetX;
            position.z = player.getPosition().z - offsetZ;
        }

        if (verticalDistance <= 1) {
            position.y = 1;
        } else {
            position.y = player.getPosition().y + verticalDistance;
        }
    }

    private float calculateHorizontalDistance() {
        return (float) (Math.abs(distanceFromPlayer * Math.cos(Math.toRadians(pitch))));
    }

    private float calculateVerticalDistance() {
        return (float) (Math.abs(distanceFromPlayer * Math.sin(Math.toRadians(pitch))));
    }

    private void calculateZoom() {
        float zoomLevel = currentMouseWheel.y;

        if (distanceFromPlayer - zoomLevel <= 5) {
            distanceFromPlayer = 5;
        } else if (distanceFromPlayer - zoomLevel >= 60) {
            distanceFromPlayer = 60;
        } else {
            distanceFromPlayer -= zoomLevel * 1.5f;
        }
    }

    private void calculatePitch() {
        if (glfwGetMouseButton(windowId, GLFW_MOUSE_BUTTON_RIGHT) == 1) {
            if (pitch <= 90 && pitch >= -45) {
                float pitchChange = mouseDy * 0.1f;
                pitch -= pitchChange;
            } else if (pitch >= 90) {
                pitch = 90;
            } else if (pitch <= -45) {
                pitch = -45;
            }
        }
    }

    private void calculateAngleAroundPlayer() {
        if (glfwGetMouseButton(windowId, GLFW_MOUSE_BUTTON_RIGHT) == 1) {
            float angleChange = mouseDx * 0.3f;
            angleAroundPlayer += angleChange;
        }
    }

    private void calculateFront() {
        front.x = (float) (Math.cos(Math.toRadians(this.yaw - 90)) * Math.cos(Math.toRadians(this.pitch)));
        front.y = (float) (Math.sin(Math.toRadians(-this.pitch)));
        front.z = (float) (Math.sin(Math.toRadians(this.yaw - 90)) * Math.cos(Math.toRadians(this.pitch)));
    }

    public Matrix4f createProjectionMatrix() {
        Matrix4f projectionMatrix = new Matrix4f();
        float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);

        return viewMatrix;
    }

    public Vector3f getFront() {
        return front;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }
}
