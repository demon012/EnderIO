package crazypants.enderio.machine.painter;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.RecipeInput;
import crazypants.enderio.machine.SlotDefinition;

public class TileEntityPainter extends AbstractPoweredTaskEntity implements ISidedInventory {

  private static final short MAX_POWER_USE_PER_TICK = 6;

  public TileEntityPainter() {
    // 0 = input slot, 1 = paint source, 2 = output slot
    super(new SlotDefinition(2, 1));
  }

  @Override
  public boolean canExtractItem(int i, ItemStack itemstack, int j) {
    return super.canExtractItem(i, itemstack, j) && PainterUtil.isMetadataEquivelent(itemstack, inventory[2]);
  }

  @Override
  public String getInvName() {
    return "Auto Painter";
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemStack) {
    if (i > 1) {
      return false;
    }
    if (i == 0) {
      return !MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), RecipeInput.create(i, itemStack)).isEmpty();
    }
    if (inventory[0] == null) {
      return BasicPainterTemplate.isValidSourceDefault(itemStack);
    }
    return MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(),
        i == 0 ? RecipeInput.create(0, itemStack) : targetInput(), i == 1 ? RecipeInput.create(1, itemStack) : paintSource()) != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPainter.unlocalisedName;
  }

  private RecipeInput targetInput() {
    return RecipeInput.create(0, inventory[0]);
  }

  private RecipeInput paintSource() {
    return RecipeInput.create(1, inventory[1]);
  }
  
  

  @Override
  protected int getNumCanMerge(ItemStack itemStack, ItemStack result) {
    if (!result.isItemEqual(inventory[2])) {
      // next result is a different item type
      return 0;
    }

    int cookedId = result.getTagCompound().getInteger(BlockPainter.KEY_SOURCE_BLOCK_ID);
    int invId = inventory[2].getTagCompound().getInteger(BlockPainter.KEY_SOURCE_BLOCK_ID);
    if (cookedId != invId) {
      // next result has a different source item than the current one
      return 0;
    }
    return itemStack.getMaxStackSize() - itemStack.stackSize;
  }

}
