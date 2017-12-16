package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

// Represents our virtual camera.
public class Camera {

  private float distanceFromPlayer = 50;
  private float angleAroundPlayer = 0;

  private Vector3f position = new Vector3f(1600, 0, 2980);
  private float pitch = 20; // up/down
  private float yaw; // left/right
  private float roll; // tilt (180 = upside down)

  private Player player;

  public Camera(Player player) {
    this.player = player;
  }

  public void move() {
    calculateZoom();
    calculatePitch();
    calculateAngleAroundPlayer();
    float horizontalDistance = calculateHorizontalDistance();
    float verticalDistance = calculateVerticalDistance();
    calculateCameraPosition(horizontalDistance, verticalDistance);
    this.yaw = 180 - (player.getRotY()) - angleAroundPlayer;
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

  private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
    float theta = player.getRotY() + angleAroundPlayer;
    float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
    float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
    position.x = player.getPosition().x - offsetX;
    position.z = player.getPosition().z - offsetZ;
    position.y = player.getPosition().y + verticalDistance;
  }

  private float calculateHorizontalDistance() {
    return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
  }

  private float calculateVerticalDistance() {
    return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
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

  private void calculateZoom() {
    float zoomLevel = Mouse.getDWheel() * 0.1f;
    distanceFromPlayer -= zoomLevel;
  }

  private void calculatePitch() {
    if (Mouse.isButtonDown(1)) {
      float pitchChange = Mouse.getDY() * 0.1f;
      pitch += pitchChange; // Inverted
    }
  }

  private void calculateAngleAroundPlayer() {
    if (Mouse.isButtonDown(1)) {
      float angleChange = Mouse.getDX() * 0.3f;
      angleAroundPlayer -= angleChange;
    }
  }

}
