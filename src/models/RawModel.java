package models;

public class RawModel {

    private int vaoID;
    private int vertexCount;
    private float[] vertexPositions;

    public RawModel(int vaoID, int vertexCount, float[] vertexPositions) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.vertexPositions = vertexPositions;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public float[] getVertexPositions() {
        return vertexPositions;
    }
}
