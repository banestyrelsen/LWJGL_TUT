package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

  public static void main(String[] args) {
    DisplayManager.createDisplay();
    Loader loader = new Loader();

    /*********** TERRAIN TEXTURE STUFF *************/
    TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
    TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
    TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
    TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

    TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
    TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

    /**********************************************/

    ModelData bunnyModelData = OBJFileLoader.loadOBJ("stanfordBunny");

    TexturedModel stanfordBunny = new TexturedModel(loader.loadToVAO(bunnyModelData.getVertices(),
        bunnyModelData.getTextureCoords(), bunnyModelData.getNormals(), bunnyModelData.getIndices()),
        new ModelTexture(loader.loadTexture("white")));

    Player player = new Player(stanfordBunny, new Vector3f(1600, 0, 2280), 0, 0, 0, 1);
    Camera camera = new Camera(player);
    ModelData dragonModelData = OBJFileLoader.loadOBJ("dragon");
    ModelData lowPolyTreeModelData = OBJFileLoader.loadOBJ("lowPolyTree");
    ModelData fernModelData = OBJFileLoader.loadOBJ("fern");
    ModelData grassModelData = OBJFileLoader.loadOBJ("grassModel");

    // RawModel rawDragonModel = loader.loadToVAO(dragonModelData.getVertices(),
    // dragonModelData.getTextureCoords(),
    // dragonModelData.getNormals(), dragonModelData.getIndices());
    // RawModel rawDragonModel = OBJLoader.loadObjModel("dragon", loader);

    TexturedModel dragon = new TexturedModel(loader.loadToVAO(dragonModelData.getVertices(),
        dragonModelData.getTextureCoords(), dragonModelData.getNormals(), dragonModelData.getIndices()),
        new ModelTexture(loader.loadTexture("green")));
    TexturedModel lowPolyTree = new TexturedModel(
        loader.loadToVAO(lowPolyTreeModelData.getVertices(), lowPolyTreeModelData.getTextureCoords(),
            lowPolyTreeModelData.getNormals(), lowPolyTreeModelData.getIndices()),
        new ModelTexture(loader.loadTexture("lowPolyTree")));
    TexturedModel fern = new TexturedModel(loader.loadToVAO(fernModelData.getVertices(),
        fernModelData.getTextureCoords(), fernModelData.getNormals(), fernModelData.getIndices()),
        new ModelTexture(loader.loadTexture("fern")));
    TexturedModel grass = new TexturedModel(loader.loadToVAO(grassModelData.getVertices(),
        grassModelData.getTextureCoords(), grassModelData.getNormals(), grassModelData.getIndices()),
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
      entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 3200, 0, random.nextFloat() * 3200), 0, 0, 0,
          .5f + random.nextFloat() * 3));
      entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 3200, 0, random.nextFloat() * 3200), 0,
          random.nextFloat() * 360, 0, .5f + random.nextFloat() * 2));
    }

    for (int i = 0; i < 500; i++) {
      entities.add(new Entity(lowPolyTree, new Vector3f(random.nextFloat() * 3200, 0, random.nextFloat() * 3200), 0,
          random.nextFloat() * 360, 0, .5f + random.nextFloat() * 3));
    }
    entities.add(new Entity(dragon, new Vector3f(1600, 0, 2400), 0, 0, 0, 1));

    Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

    List<Terrain> terrains = new ArrayList<>();
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        Terrain terrain = new Terrain(x, y, loader, texturePack, blendMap);
        terrains.add(terrain);
      }
    }

    MasterRenderer renderer = new MasterRenderer();
    while (!Display.isCloseRequested()) {
      camera.move();
      player.move();
      renderer.processEntity(player);
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
