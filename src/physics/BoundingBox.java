package physics;

import lwjglUtil.vector.Vector3f;
import renderEngine.ModelData;

public class BoundingBox {

    private float xMin;
    private float yMin;
    private float zMin;

    private float xMax;
    private float yMax;
    private float zMax;

    private float[] vertices;

    private Vector3f size;
    private Vector3f center;

    float[] positions = new float[]{
            // VO
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,
    };

    int[] indices = new int[]{
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            4, 0, 3, 5, 4, 3,
            // Right face
            3, 2, 7, 5, 3, 7,
            // Left face
            6, 1, 0, 6, 0, 4,
            // Bottom face
            2, 1, 6, 2, 6, 7,
            // Back face
            7, 6, 4, 7, 4, 5,
    };

    public BoundingBox(float[] vertices) {
        this.vertices = vertices;
    }

    public ModelData calculate() {
        xMin = vertices[0];
        xMax = vertices[0];

        yMin = vertices[1];
        yMax = vertices[1];

        zMin = vertices[2];
        zMax = vertices[2];

        for (int i = 0; i < vertices.length / 3; i++) {
            if (vertices[i * 3] < xMin) {
                xMin = vertices[i * 3];
            }

            if (vertices[i * 3] > xMax) {
                xMax = vertices[i * 3];
            }

            if (vertices[i * 3 + 1] < yMin) {
                yMin = vertices[i * 3 + 1];
            }

            if (vertices[i * 3 + 1] > yMax) {
                yMax = vertices[i * 3 + 1];
            }

            if (vertices[i * 3 + 2] < zMin) {
                zMin = vertices[i * 3 + 2];
            }

            if (vertices[i * 3 + 2] > zMax) {
                zMax = vertices[i * 3 + 2];
            }
        }

        size = new Vector3f(xMax - xMin, yMax - yMin, zMax - zMin);
        center = new Vector3f((xMin + xMin) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);

        float[] scaledPositions = new float[positions.length];

        for (int i = 0; i < positions.length / 3; i++) {
            scaledPositions[i * 3] = (positions[i * 3] * size.x);
            scaledPositions[i * 3 + 1] = (positions[i * 3 + 1] * size.y);
            scaledPositions[i * 3 + 2] = (positions[i * 3 + 2] * size.z);
        }

        return new ModelData(scaledPositions, new float[scaledPositions.length], new float[scaledPositions.length], indices, 0);
    }

    public Vector3f getCenter() {
        return center;
    }

    public float getxMin() {
        return xMin;
    }

    public float getyMin() {
        return yMin;
    }

    public float getzMin() {
        return zMin;
    }

    public float getxMax() {
        return xMax;
    }

    public float getyMax() {
        return yMax;
    }

    public float getzMax() {
        return zMax;
    }
}
