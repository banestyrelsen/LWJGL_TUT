package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

// Represents our virtual camera.
public class Camera {

  private Vector3f position = new Vector3f(0, 200, 800);
  private float pitch; // up/down
  private float yaw; // left/right
  private float roll; // tilt (180 = upside down)

  public Camera() {
  }

  public void move() {
  }

  public Vector3f getPosition() {
    return position;
  }

  public float getPitch() {
    return pitch;
  }

  public float getYaw() {
    return yaw;
  }

  public float getRoll() {
    return roll;
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }

  public void setYaw(float yaw) {
    this.yaw = yaw;
  }

  public void setRoll(float roll) {
    this.roll = roll;
  }
 
}
