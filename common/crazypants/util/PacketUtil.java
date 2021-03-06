package crazypants.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import crazypants.enderio.EnderIO;

public class PacketUtil {

  public static Packet createTileEntityPacket(String channel, int id, TileEntity te) {

    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(id);
      dos.writeInt(te.xCoord);
      dos.writeInt(te.yCoord);
      dos.writeInt(te.zCoord);
      NBTTagCompound root = new NBTTagCompound();
      te.writeToNBT(root);
      writeNBTTagCompound(root, dos);

    } catch (IOException e) {
      // never thrown
    }

    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = channel;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;

  }

  public static TileEntity handleTileEntityPacket(boolean readId, DataInputStream dis) {
    int x;
    int y;
    int z;
    try {
      if (readId) {
        int id = dis.readInt();
      }
      x = dis.readInt();
      y = dis.readInt();
      z = dis.readInt();
    } catch (IOException e) {
      FMLCommonHandler.instance().raiseException(e, "PacketUtil.readTileEntityPacket", false);
      return null;
    }
    NBTTagCompound tags = readNBTTagCompound(dis);

    World world = EnderIO.proxy.getClientWorld();
    if (world == null) {
      FMLLog.finer("PacketUtil.handleTileEntityPacket: Recieved packet for tile that did not exist or was not loaded on client.", (Object[])null);
      return null;
    }
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te == null) {
      FMLCommonHandler.instance().raiseException(new Exception("TileEntity was null"), "PacketUtil.readTileEntityPacket", true);
      return null;
    }
    te.readFromNBT(tags);
    return te;
  }

  public static ItemStack readItemStack(DataInputStream dataIn) throws IOException {
    ItemStack var2 = null;
    short itemID = dataIn.readShort();

    if (itemID >= 0) {
      byte stackSize = dataIn.readByte();
      short damage = dataIn.readShort();
      var2 = new ItemStack(itemID, stackSize, damage);
      var2.stackTagCompound = readNBTTagCompound(dataIn);
    }

    return var2;
  }

  public static byte[] readByteArray(int length, DataInputStream dataIn) throws IOException {

    byte[] barray = new byte[length];
    dataIn.readFully(barray, 0, length);
    return barray;

  }

  public static void writeItemStack(ItemStack spawnstack, DataOutputStream dataout) throws IOException {
    if (spawnstack == null) {
      dataout.writeShort(-1);
    } else {
      dataout.writeShort(spawnstack.itemID);
      dataout.writeByte(spawnstack.stackSize);
      dataout.writeShort(spawnstack.getItemDamage());
      writeNBTTagCompound(spawnstack.stackTagCompound, dataout);
    }
  }

  public static NBTTagCompound readNBTTagCompound(DataInputStream dataIn) {
    try {
      short var2 = dataIn.readShort();
      if (var2 < 0) {
        return null;
      } else {
        byte[] var3 = readByteArray(var2, dataIn);
        return CompressedStreamTools.decompress(var3);
      }
    } catch (IOException e) {
      FMLCommonHandler.instance().raiseException(e, "Custom Packet", true);
      return null;
    }
  }

  public static void writeNBTTagCompound(NBTTagCompound compound, DataOutputStream dataout) {
    try {
      if (compound == null) {
        dataout.writeShort(-1);
      } else {
        byte[] var3 = CompressedStreamTools.compress(compound);
        dataout.writeShort((short) var3.length);
        dataout.write(var3);
      }
    } catch (IOException e) {
      FMLCommonHandler.instance().raiseException(e, "PacketUtil.readTileEntityPacket.writeNBTTagCompound", true);
    }
  }

}
