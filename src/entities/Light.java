package entities;

import lwjglUtil.vector.Vector3f;

public class Light {

    private Vector3f position;
    private Vector3f colour;
    private Vector3f attenuation = new Vector3f(1, 0, 0);
    private float coneAngle;
    private Vector3f coneDirection = new Vector3f(1, 0, 0);

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Light(Vector3f position, Vector3f colour, Vector3f attentuation) {
        this.position = position;
        this.colour = colour;
        this.attenuation = attentuation;
    }


    public Light(Vector3f position, Vector3f colour, Vector3f attenuation, float coneAngle, Vector3f coneDirection) {
        this.position = position;
        this.colour = colour;
        this.attenuation = attenuation;
        this.coneDirection = coneDirection;
        this.coneAngle = coneAngle;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getConeAngle() {
        return coneAngle;
    }

    public void setConeAngle(float coneAngle) {
        this.coneAngle = coneAngle;
    }
}
