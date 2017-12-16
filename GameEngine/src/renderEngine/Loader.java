package renderEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import models.RawModel;

/**
 * Loads 3D models into memory by storing positional data about the model in a
 * VAO.
 * 
 * @author v
 *
 */
public class Loader {

  private List<Integer> vaos = new ArrayList<Integer>();
  private List<Integer> vbos = new ArrayList<Integer>();
  private List<Integer> textures = new ArrayList<Integer>();

  public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
    int vaoID = createVAO();
    bindIndicesBuffer(indices);
    storeDataInAttributeList(0, 3, positions);
    storeDataInAttributeList(1, 2, textureCoords);
    storeDataInAttributeList(2, 3, normals);
    unbindVAO();
    return new RawModel(vaoID, indices.length); // Divided by 3 because each vertex has three floats (x,y,z)
  }

  public int loadTexture(String fileName) {
    Texture texture = null;
    try {
      System.out.println("Loading file \"res/" + fileName + "\"");
      BufferedImage bufferedImage = ImageIO.read(new File("res/" + fileName + ".png"));
      texture = BufferedImageUtil.getTexture("", bufferedImage);
//      texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
      GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
      GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.0f);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    int textureID = texture.getTextureID();
    textures.add(textureID);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    return textureID;
  }

  public void cleanUp() {
    for (int vao : vaos) {
      GL30.glDeleteVertexArrays(vao);
    }
    for (int vbo : vbos) {
      GL15.glDeleteBuffers(vbo);
    }
    for (int texture : textures) {
      GL11.glDeleteTextures(texture);
    }
  }

  private int createVAO() {
    int vaoID = GL30.glGenVertexArrays();
    vaos.add(vaoID);
    GL30.glBindVertexArray(vaoID);
    return vaoID;
  }

  private void bindIndicesBuffer(int[] indices) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
    IntBuffer buffer = storeDataInIntBuffer(indices);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
  }

  private IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data);
    buffer.flip(); // Ready to read
    return buffer;
  }

  private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    FloatBuffer buffer = this.storeDataInFloatBuffer(data);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
  }

  private void unbindVAO() {
    GL30.glBindVertexArray(0);
  }

  private FloatBuffer storeDataInFloatBuffer(float[] data) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip(); // We've finished writing to the buffer and we are ready to read from it
    return buffer;
  }
}
