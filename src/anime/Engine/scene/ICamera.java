package anime.Engine.scene;

import lwjglUtil.vector.Matrix4f;

public interface ICamera {
	
	public Matrix4f getViewMatrix();
	public Matrix4f getProjectionMatrix();
	public Matrix4f getProjectionViewMatrix();
	public void move();
	public void input();

}
