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
import terrains.Terrain;
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
//    for (int i = 0; i < 200; i++) {
//      float x = random.nextFloat() * 100 - 50;
//      float y = random.nextFloat() * 100 - 50;
//      float z = random.nextFloat() * -300;

//      dragons.add(
//          new Entity(dragonModel, new Vector3f(x, y, z), random.nextFloat() * 180f, random.nextFloat() * 180f, 0f, 1f));
//    }

  dragons.add(
  new Entity(dragonModel, new Vector3f(500, 0, 500), 0, 0, 0, 5));
    
    Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
    Terrain terrain = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("grass")));
    Terrain terrain2 = new Terrain(1, 0, loader, new ModelTexture(loader.loadTexture("grass")));

    Camera camera = new Camera();
    camera.setPosition(new Vector3f(500, 25, 520));
    camera.setPitch(45);

    MasterRenderer renderer = new MasterRenderer();
    while (!Display.isCloseRequested()) {
      camera.move();
      // game logic
      // render
      renderer.processTerrain(terrain);
      renderer.processTerrain(terrain2);
      for (Entity dragon : dragons) {
        dragon.increaseRotation(0, 1, 0);
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
