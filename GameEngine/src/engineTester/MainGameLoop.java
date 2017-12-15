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

    RawModel rawDragonModel = OBJLoader.loadObjModel("dragon", loader);
    TexturedModel dragon = new TexturedModel(rawDragonModel, new ModelTexture(loader.loadTexture("green")));
    TexturedModel lowPolyTree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),
        new ModelTexture(loader.loadTexture("lowPolyTree")));
    TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
        new ModelTexture(loader.loadTexture("fern")));
    TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
        new ModelTexture(loader.loadTexture("grassTexture")));
    grass.getTexture()
        .setHasTransparency(true);
    grass.getTexture()
        .setUseFakeLighting(true);

    fern.getTexture()
        .setHasTransparency(true);
    fern.getTexture()
        .setUseFakeLighting(true);
    ModelTexture texture = dragon.getTexture();
    texture.setShineDamper(10);
    texture.setReflectivity(1);

    List<Entity> entities = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < 5000; i++) {
      entities
          .add(new Entity(grass, new Vector3f(random.nextFloat() * 3200, 0, random.nextFloat() * 3200), 0, 0, 0, 3));
      entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 3200, 0, random.nextFloat() * 3200), 0,
          random.nextFloat() * 360, 0, 1.6f));
    }

    for (int i = 0; i < 500; i++) {
      entities.add(new Entity(lowPolyTree, new Vector3f(random.nextFloat() * 3200, 0, random.nextFloat() * 3200), 0,
          random.nextFloat() * 360, 0, random.nextFloat() * 3));
    }
    entities.add(new Entity(dragon, new Vector3f(1600, 0, 2400), 0, 0, 0, 1));

    Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

    List<Terrain> terrains = new ArrayList<>();
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        Terrain terrain = new Terrain(x, y, loader, new ModelTexture(loader.loadTexture("dirt")));
        terrains.add(terrain);
      }
    }

    Camera camera = new Camera();
    camera.setPosition(new Vector3f(1600, 25, 2480));
    camera.setPitch(0);

    MasterRenderer renderer = new MasterRenderer();
    while (!Display.isCloseRequested()) {
      camera.move();
      // game logic
      // render
      for (Terrain terrain : terrains) {
        renderer.processTerrain(terrain);
      }
      for (Entity entity : entities) {
        // entity.increaseRotation(0, 1, 0);
        renderer.processEntity(entity);
      }
      renderer.render(light, camera);
      DisplayManager.updateDisplay();
    }

    renderer.cleanUp();
    loader.cleanUp();
    DisplayManager.closeDisplay();
  }

}
