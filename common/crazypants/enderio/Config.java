package crazypants.enderio;

import crazypants.vecmath.VecmathUtil;
import net.minecraftforge.common.Configuration;

public final class Config {

  static int BID = 700;
  static int IID = 8524;
  
  
  public static final double DEFAULT_CONDUIT_SCALE = 0.2; 
  
  public static boolean useAlternateBinderRecipe;
  
  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static void load(Configuration config) {
    for (ModObject e : ModObject.values()) {
      e.load(config);
    }
    useAlternateBinderRecipe = config.get("Settings", "useAlternateBinderRecipe", false).getBoolean(false);
    conduitScale = config.get("Settings", "conduitScale", DEFAULT_CONDUIT_SCALE, 
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
        "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);
  }

  private Config() {
  }

}
