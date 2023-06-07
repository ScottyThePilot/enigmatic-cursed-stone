package scottythepilot.enigmaticcursedstone;

import com.integral.enigmaticlegacy.EnigmaticLegacy;
import com.integral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.integral.enigmaticlegacy.items.CursedRing;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(EnigmaticCursedStone.MOD_ID)
public class EnigmaticCursedStone {
  public static final String MOD_ID = "enigmaticcursedstone";
  public static final Logger LOGGER = LogUtils.getLogger();

  public static Item cursedStoneItem;

  public EnigmaticCursedStone() {}

  @Mod.EventBusSubscriber(modid = MOD_ID)
  public static class Handler {
    // Enigmatic Legacy's event priority is LOW, so this should execute after it
    @SubscribeEvent(priority = EventPriority.LOWEST)
	  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
      Player player = event.getPlayer();
      if (
        // Only run if enigmatic legacy has given the player the ring (won't happen if the item is disabled)
        SuperpositionHandler.hasPersistentTag(player, "enigmaticlegacy.firstjoin") &&
        // Don't run if we've already replaced the ring
        !SuperpositionHandler.hasPersistentTag(player, "enigmaticcursedstone.firstjoin") &&
        // Don't run if ultra-hardcore is enabled, where the ring is auto-equipped
        // If a player enables ultra-hardcore mode, then they probably don't want the cursed stone
        !CursedRing.ultraHardcore.getValue()
      ) {
        ItemStack cursedRingStack = new ItemStack(EnigmaticLegacy.cursedRing);
        ItemStack cursedStoneStack = new ItemStack(cursedStoneItem);

        int slot = player.getInventory().findSlotMatchingItem(cursedRingStack);
        if (slot != -1) player.getInventory().setItem(slot, cursedStoneStack);

        SuperpositionHandler.setPersistentBoolean(player, "enigmaticcursedstone.firstjoin", true);
      }
    }
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID)
  public static class RegistryEvents {
    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
      cursedStoneItem = new Item(properties(16, Rarity.UNCOMMON)).setRegistryName(MOD_ID, "cursed_stone");
      event.getRegistry().registerAll(cursedStoneItem);
    }

    private static Item.Properties properties(int stacks, Rarity rarity) {
      return new Item.Properties().tab(EnigmaticLegacy.enigmaticTab).stacksTo(stacks).rarity(rarity);
    }
  }
}
