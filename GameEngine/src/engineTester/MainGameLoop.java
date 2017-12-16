package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
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

  /**** FPS COUNTER ****/
  private static int frames = 0;
  private static long timer = System.currentTimeMillis();
  private static int TERRAIN_SIZE = 800;
  private static final int N_TERRAINS = 6;
  private static Terrain[][] terrains = new Terrain[N_TERRAINS][N_TERRAINS];

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
    ModelData dragonModelData = OBJFileLoader.loadOBJ("dragon");
    ModelData lowPolyTreeModelData = OBJFileLoader.loadOBJ("lowPolyTree");
    ModelData fernModelData = OBJFileLoader.loadOBJ("fern");
    ModelData grassModelData = OBJFileLoader.loadOBJ("grassModel");
    ModelData boxModelData = OBJFileLoader.loadOBJ("box");

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
    TexturedModel box = new TexturedModel(loader.loadToVAO(boxModelData.getVertices(), boxModelData.getTextureCoords(),
        boxModelData.getNormals(), boxModelData.getIndices()), new ModelTexture(loader.loadTexture("box")));
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

    Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

    List<Terrain> terrainList = new ArrayList<Terrain>();
    for (int x = 0; x < N_TERRAINS; x++) {
      for (int y = 0; y < N_TERRAINS; y++) {
        Terrain terrain = new Terrain(x, y, loader, texturePack, blendMap, "heightmap");
        terrains[x][y] = terrain;
        terrainList.add(terrain);
      }
    }

    Player player = new Player(stanfordBunny, new Vector3f(400, terrains[0][0].getHeightOfTerrain(400, 400), 400), 0, 0,
        0, 1);
    Camera camera = new Camera(player);

    List<Entity> entities = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < 3200; i++) {
      int terrainIndexX = random.nextInt(N_TERRAINS);
      int terrainIndexZ = random.nextInt(N_TERRAINS);

      Terrain terrain = terrains[terrainIndexX][terrainIndexZ];
      float x = (random.nextFloat() * TERRAIN_SIZE + (TERRAIN_SIZE * (terrainIndexX)));
      float z = (random.nextFloat() * TERRAIN_SIZE + (TERRAIN_SIZE * (terrainIndexZ)));

      float y = terrain.getHeightOfTerrain(x, z);
      TexturedModel model = grass;
      if (i % 15 == 0) {
        model = box;
      } else if (i % 5 == 0) {
        model = lowPolyTree;
      } else if (i % 3 == 0) {
        model = fern;
      }
      entities.add(new Entity(model, new Vector3f(x, y, z), 0, 0, 0, 1.f));
    }

    MasterRenderer renderer = new MasterRenderer();

    while (!Display.isCloseRequested()) {
      // game logic
      // render

      camera.move();
      renderer.processEntity(player);

      player.move(getTerrain(player.getPosition().x, player.getPosition().z));
      for (Terrain terrain : terrainList) {
        renderer.processTerrain(terrain);
      }
      for (Entity entity : entities) {
        renderer.processEntity(entity);
      }
      renderer.render(light, camera);
      DisplayManager.updateDisplay();
      getFps(player);
    }
    renderer.cleanUp();
    loader.cleanUp();
    DisplayManager.closeDisplay();
  }

  private static void getFps(Player player) {
    frames++;
    if (System.currentTimeMillis() - timer > 1000) {
      timer += 1000;
      DisplayManager.setInfoInTitle(frames, player.getPosition());
      frames = 0;
    }
  }

  private static Terrain getTerrain(float entityX, float entityZ) {
    int terrainGridX = (int) (entityX / TERRAIN_SIZE);
    int terrainGridZ = (int) (entityZ / TERRAIN_SIZE);
    Terrain terrain = terrains[terrainGridX][terrainGridZ];

    return terrain;
  }

}
