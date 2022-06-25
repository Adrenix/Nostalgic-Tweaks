package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.MixinConfig;
import mod.adrenix.nostalgic.client.screen.ClassicProgressScreen;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
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
        if (!MixinConfig.Candy.oldLoadingScreens())
            return genericScreen;

        ClassicProgressScreen progressScreen = new ClassicProgressScreen(new ProgressScreen(false));
        progressScreen.setHeader(Component.translatable(NostalgicLang.Gui.LEVEL_LOADING));
        progressScreen.setStage(Component.translatable(NostalgicLang.Vanilla.READ_WORLD_DATA));
        progressScreen.setPauseTicking(ClassicProgressScreen.NO_PAUSES);

        return progressScreen;
    }
}
