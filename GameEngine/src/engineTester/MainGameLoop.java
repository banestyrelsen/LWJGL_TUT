package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import textures.ModelTexture;

public class MainGameLoop {

  public static void main(String[] args) {
    DisplayManager.createDisplay();
    Loader loader = new Loader();

    RawModel model = OBJLoader.loadObjModel("dragon", loader);
    TexturedModel dragonModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("green")));
    ModelTexture texture = dragonModel.getTexture();
    texture.setShineDamper(10);
    texture.setReflectivity(1);

    List<Entity> dragons = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < 200; i++) {
      float x = random.nextFloat() * 100 - 50;
      float y = random.nextFloat() * 100 - 50;
      float z = random.nextFloat() * -300;

      dragons.add(
          new Entity(dragonModel, new Vector3f(x, y, z), random.nextFloat() * 180f, random.nextFloat() * 180f, 0f, 1f));
    }

    Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
    Camera camera = new Camera();
    MasterRenderer renderer = new MasterRenderer();
    while (!Display.isCloseRequested()) {
      camera.move();
      // game logic
      // render
      for (Entity dragon : dragons) {
        renderer.processEntity(dragon);
      }
      renderer.render(light, camera);
      DisplayManager.updateDisplay();
    }

    renderer.cleanUp();
    loader.cleanUp();
    DisplayManager.closeDisplay();
  }

}
