package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

// Represents our virtual camera.
public class Camera {

  private Vector3f position = new Vector3f(0, 0, 0);
  private float pitch; // up/down
  private float yaw; // left/right
  private float roll; // tilt (180 = upside down)

  public Camera() {
  }

  public void move() {
    int dWheel = Mouse.getDWheel();
    if (dWheel < 0) {
      position.z += 5.25f;
    } else if (dWheel > 0) {
      position.z -= 5.25f;
    }

    if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
      position.y -= -1.25f;
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
      position.y -= +1.25f;
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
      position.x -= +1.25f;
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
      position.x -= -1.25f;
    }

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

}
