package crazypants.enderio;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.render.ConduitRenderer;
import crazypants.util.DebugGuiTPS;

public class CommonProxy {

  private final ServerTickHandler serverTickHandler = new ServerTickHandler();

  private static final DecimalFormat FORMAT = new DecimalFormat("########0.000");

  private boolean showTpdGUI = false;

  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  public void load() {
    TickRegistry.registerTickHandler(serverTickHandler, Side.SERVER);

    if (showTpdGUI) {
      DebugGuiTPS.showTpsGUI();
    }

  }

  public ConduitRenderer getRendererForConduit(IConduit conduit) {
    return null;
  }

  public ServerTickHandler getServerTickHandler() {
    return serverTickHandler;
  }

  public void serverStopped() {
    serverTickHandler.serverStopped();
  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;

  }

}
