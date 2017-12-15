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
    TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
        new ModelTexture(loader.loadTexture("grassTexture")));
    grass.getTexture().setHasTransparency(true);
    grass.getTexture().setUseFakeLighting(true);
    TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
        new ModelTexture(loader.loadTexture("fern")));
    fern.getTexture().setHasTransparency(true);
    fern.getTexture().setUseFakeLighting(true);
    ModelTexture texture = dragonModel.getTexture();
    texture.setShineDamper(10);
    texture.setReflectivity(1);

    List<Entity> entities = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < 1500; i++) {
      entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 1600, 0, random.nextFloat() * 1600),
          0, 0, 0, 1));
      entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 1600, 0, random.nextFloat() * 1600),
          0, 0, 0, 0.6f));
    }

    entities.add(new Entity(dragonModel, new Vector3f(800, 0, 800), 0, 0, 0, 1));
    
    Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
    Terrain terrain = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("dirt")));
    Terrain terrain2 = new Terrain(1, 0, loader, new ModelTexture(loader.loadTexture("dirt")));
    Terrain terrain3 = new Terrain(0, 1, loader, new ModelTexture(loader.loadTexture("dirt")));
    Terrain terrain4 = new Terrain(1, 1, loader, new ModelTexture(loader.loadTexture("dirt")));
    Camera camera = new Camera();
    camera.setPosition(new Vector3f(800, 25, 820));
    camera.setPitch(0);

    MasterRenderer renderer = new MasterRenderer();
    while (!Display.isCloseRequested()) {
      camera.move();
      // game logic
      // render
      renderer.processTerrain(terrain);
      renderer.processTerrain(terrain2);
      renderer.processTerrain(terrain3);
      renderer.processTerrain(terrain4);
      for (Entity entity : entities) {
//        entity.increaseRotation(0, 1, 0);
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
