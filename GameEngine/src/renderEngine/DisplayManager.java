package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

public class DisplayManager {

  private static final int WIDTH = 1600;
  private static final int HEIGHT = 1200;
  private static final int FPS_CAP = 2000;
  private static long lastFrameTime;
  private static float delta;

  public static void createDisplay() {
    ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true)
        .withProfileCore(true);

    try {
      Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
      Display.create(new PixelFormat(), attribs);
      Display.setTitle("Clade test");
    } catch (LWJGLException e) {
      e.printStackTrace();
    }

    GL11.glViewport(0, 0, WIDTH, HEIGHT);
    lastFrameTime = getCurrentTime();
  }

  public static void setInfoInTitle(int fps, Vector3f playerPosition) {
    Display.setTitle("Clade test | (" + (int)playerPosition.getX() + " , " + (int)playerPosition.getY() + " , "
        + (int)playerPosition.getZ() + ") | " + fps + " FPS");
  }

  public static void updateDisplay() {
    Display.sync(FPS_CAP);
    Display.update();
    long currentFrameTime = getCurrentTime();
    delta = (currentFrameTime - lastFrameTime) / 1000f;
    lastFrameTime = currentFrameTime;
  }

  public static float getFrameTimeSeconds() {
    return delta;
  }

  public static void closeDisplay() {
    Display.destroy();
  }

  private static long getCurrentTime() {
    return Sys.getTime() * 1000 / Sys.getTimerResolution();
  }
}
