package crazypants.enderio.conduit.render;

import static crazypants.util.ForgeDirectionOffsets.offsetScaled;
import static net.minecraftforge.common.ForgeDirection.SOUTH;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.render.BoundingBox;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vertex;

public class RoundedSegmentRenderer {

  private static Vertex[][] DIR_COORDS = new Vertex[ForgeDirection.VALID_DIRECTIONS.length][];

  private static final Vector3d REF_TRANS = new Vector3d(0.5, 0.5, 0.5);

  static {
    double circ = ConduitGeometryUtil.WIDTH * 0.7;

    Vertex[] refCoords = createUnitSectionQuads(16, -0.25, 0.25);

    for (Vertex coord : refCoords) {
      coord.xyz.x = coord.xyz.x * circ;
      coord.xyz.y = coord.xyz.y * circ;
    }
    Matrix4d rotMat = new Matrix4d();
    rotMat.setIdentity();
    rotMat.setTranslation(REF_TRANS);

    DIR_COORDS[SOUTH.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.SOUTH, 0.25));

    rotMat.makeRotationY(Math.PI);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.NORTH.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.NORTH, 0.25));

    rotMat.makeRotationY(Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.EAST.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.EAST, 0.25));

    rotMat.makeRotationY(-Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.WEST.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.WEST, 0.25));

    rotMat.makeRotationX(-Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.UP.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.UP, 0.25));

    rotMat.makeRotationX(Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.DOWN.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.DOWN, 0.25));

  }

  private static Vertex[] xformCoords(Vertex[] refCoords, Matrix4d rotMat, Vector3d trans) {
    Vertex[] res = new Vertex[refCoords.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = new Vertex(refCoords[i]);
      res[i].transform(rotMat);
      res[i].translate(trans);
    }
    return res;
  }

  private static Vertex[] xformCoords(List<Vertex> refCoords, Matrix4d rotMat, Vector3d trans) {
    Vertex[] res = new Vertex[refCoords.size()];
    for (int i = 0; i < res.length; i++) {
      res[i] = new Vertex(refCoords.get(i));
      res[i].transform(rotMat);
      res[i].translate(trans);
    }
    return res;
  }

  public static Vertex[] createUnitCrossSection(double xOffset, double yOffset, double zOffset, int numCoords, int u) {

    Vertex[] crossSection = new Vertex[numCoords];

    double angle = 0;
    double inc = (Math.PI * 2) / (crossSection.length - 1);
    for (int i = 0; i < crossSection.length; i++) {
      double x = Math.cos(angle) * 0.5;
      double y = Math.sin(angle) * 0.5;
      angle += inc;
      crossSection[i] = new Vertex();
      crossSection[i].setXYZ(xOffset + x, yOffset + y, zOffset);
      crossSection[i].setNormal(x, y, 0);
      crossSection[i].setUV(u, y + 0.5);
    }
    return crossSection;

  }

  public static void renderSegment(ForgeDirection dir, BoundingBox bounds, float minU, float maxU, float minV, float maxV) {
    float uScale = maxU - minU;
    float vScale = maxV - minV;

    Vector3d offset = calcOffset(dir, bounds);

    Tessellator tes = Tessellator.instance;
    Vertex[] coords = DIR_COORDS[dir.ordinal()];
    for (Vertex coord : coords) {
      double u = minU + (coord.uv.x * uScale);
      double v = minV + (coord.uv.y * vScale);
      tes.setNormal(coord.normal.x, coord.normal.y, coord.normal.z);
      tes.addVertexWithUV(offset.x + coord.xyz.x, offset.y + coord.xyz.y, offset.z + coord.xyz.z, u, v);
    }
  }

  private static Vector3d calcOffset(ForgeDirection dir, BoundingBox bounds) {
    Vector3d res = new Vector3d();
    Vector3d center = bounds.getCenter();
    if (dir != ForgeDirection.UP && dir != ForgeDirection.DOWN) {
      res.set(0, center.y - REF_TRANS.y, 0);
    } else {
      res.set(center.x - REF_TRANS.x, 0, 0);
    }
    return res;
  }

  public static Vertex[] createUnitSectionQuads(int numCoords, double minZ, double maxZ) {

    Vertex[] z0 = createUnitCrossSection(0, 0, minZ, numCoords + 1, 0);
    Vertex[] z1 = createUnitCrossSection(0, 0, maxZ, numCoords + 1, 1);

    Vertex[] result = new Vertex[numCoords * 4];
    for (int i = 0; i < numCoords; i++) {
      int index = i * 4;
      result[index] = new Vertex(z0[i]);
      result[index + 1] = new Vertex(z0[i + 1]);
      result[index + 2] = new Vertex(z1[i + 1]);
      result[index + 3] = new Vertex(z1[i]);
    }
    return result;

  }

}
