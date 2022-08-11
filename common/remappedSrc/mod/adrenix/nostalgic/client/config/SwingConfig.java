package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.SwingTweak;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.world.item.*;

import java.util.Map;

/**
 * Pulls Minecraft client code out of the {@link ModConfig} class. This utility allows the client to interface with the
 * mod's swing speed configurations.
 *
 * This class is used exclusively by the client. There is no reason for the server to interface with this.
 */

public abstract class SwingConfig
{
    private static final ClientConfig.Swing SWING = ClientConfigCache.getSwing();
    private static int getSpeedFromItem(Item item)
    {
        Map.Entry<String, Integer> entry = CustomSwings.getEntryFromItem(item);

        if (isSpeedGlobal())
            return SWING.global;
        else if (entry != null)
            return entry.getValue();
        else if (item instanceof SwordItem)
            return SWING.sword;
        else if (item instanceof BlockItem)
            return SWING.block;
        else if (item instanceof MiningToolItem)
            return SWING.tool;
        return SWING.item;
    }

    public static int getSwingSpeed(AbstractClientPlayerEntity player)
    {
        if (ModConfig.isModEnabled())
            return getSpeedFromItem(player.getMainHandStack().getItem());
        return DefaultConfig.Swing.NEW_SPEED;
    }

    public static boolean isOverridingFatigue() { return ModConfig.isModEnabled() && SWING.fatigue != DefaultConfig.Swing.GLOBAL; }
    public static boolean isOverridingSpeeds() { return !ModConfig.isTweakOn(SwingTweak.OVERRIDE_SPEEDS) || SWING.overrideSpeeds; }
    public static boolean isOverridingHaste() { return ModConfig.isModEnabled() && SWING.haste != DefaultConfig.Swing.GLOBAL; }
    public static boolean isSpeedGlobal() { return SWING.global != DefaultConfig.Swing.GLOBAL; }
    public static int getFatigueSpeed() { return isSpeedGlobal() ? SWING.global : SWING.fatigue; }
    public static int getHasteSpeed() { return isSpeedGlobal() ? SWING.global : SWING.haste; }
    public static int getSwingSpeed() { return getSwingSpeed(MinecraftClient.getInstance().player); }
    public static int getGlobalSpeed() { return SWING.global; }
}
