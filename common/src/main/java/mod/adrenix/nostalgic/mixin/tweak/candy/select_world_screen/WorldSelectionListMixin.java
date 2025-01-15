package mod.adrenix.nostalgic.mixin.tweak.candy.select_world_screen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldSelectionList.class)
public abstract class WorldSelectionListMixin
{
    /**
     * Prevents the world creation screen from being opened if the old world selection screens are being used.
     */
    @WrapWithCondition(
        method = "loadLevels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;openFresh(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;)V"
        )
    )
    private boolean nt_select_world_screen$onCreateWorldScreen(Minecraft minecraft, Screen lastScreen)
    {
        return CandyTweak.OLD_WORLD_SELECT_SCREEN.get() == Generic.MODERN && !CandyTweak.LEVEL_SELECT_WHEN_EMPTY.get();
    }
}
