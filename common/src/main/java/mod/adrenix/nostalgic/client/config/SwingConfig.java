package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;

import java.util.Map;

/**
 * Pulls Minecraft client code out of the {@link MixinConfig} class.
 *
 * While having the code there was fine since the server didn't class load the utility, this removes the
 * possibility have running into issues in the future.
 *
 * This class is used exclusively by the client. There is zero reason for the server to interface with this.
 */

public abstract class SwingConfig
{
    private static final ClientConfig.Swing SWING = ClientConfigCache.getSwing();
    private static int getSpeedFromItem(Item item)
    {
        Map.Entry<String, Integer> entry = CustomSwings.getEntryFromItem(item);

        if (MixinConfig.Swing.isSpeedGlobal())
            return SWING.global;
        else if (entry != null)
            return entry.getValue();
        else if (item instanceof SwordItem)
            return SWING.sword;
        else if (item instanceof BlockItem)
            return SWING.block;
        else if (item instanceof DiggerItem)
            return SWING.tool;
        return SWING.item;
    }

    public static int getSwingSpeed(AbstractClientPlayer player)
    {
        if (MixinConfig.isModEnabled(null))
            return getSpeedFromItem(player.getMainHandItem().getItem());
        return DefaultConfig.Swing.NEW_SPEED;
    }

    public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getInstance().player); }
}
