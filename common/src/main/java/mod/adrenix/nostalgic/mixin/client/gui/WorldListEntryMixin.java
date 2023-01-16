package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.client.screen.NostalgicProgressScreen;
import mod.adrenix.nostalgic.util.common.ComponentBackport;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListEntryMixin
{
    /**
     * Redirects the "Reading world data..." generic screen to static classic progress level loading screen.
     * Controlled by the old loading screen tweak.
     */
    @ModifyArg
    (
        method = "queueLoadScreen",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;forceSetScreen(Lnet/minecraft/client/gui/screens/Screen;)V"
        )
    )
    private Screen NT$onQueueLoadScreen(Screen genericScreen)
    {
        if (!ModConfig.Candy.oldLoadingScreens())
            return genericScreen;

        NostalgicProgressScreen progressScreen = new NostalgicProgressScreen(new ProgressScreen(false));
        progressScreen.setHeader(ComponentBackport.translatable(LangUtil.Gui.LEVEL_LOADING));
        progressScreen.setStage(ComponentBackport.translatable(LangUtil.Vanilla.READ_WORLD_DATA));
        progressScreen.setPauseTicking(NostalgicProgressScreen.NO_PAUSES);

        return progressScreen;
    }
}
