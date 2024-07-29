package mod.adrenix.nostalgic.mixin.tweak.candy.window_title;

import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Adds runnable that will update the window title text when the client config is saved.
     */
    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void nt_window_title$onSave(CallbackInfo callback)
    {
        AfterConfigSave.addInstruction(() -> Minecraft.getInstance().updateTitle());
    }

    /**
     * Changes the game's OS window title text.
     */
    @ModifyArg(
        method = "updateTitle",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/Window;setTitle(Ljava/lang/String;)V"
        )
    )
    private String nt_window_title$setUpdatedTitle(String title)
    {
        if (!CandyTweak.ENABLE_WINDOW_TITLE.get())
            return title;

        final String VERSION = GameUtil.getVersion();

        if (CandyTweak.MATCH_VERSION_OVERLAY.get())
            return TextUtil.limit(CandyTweak.OLD_OVERLAY_TEXT.parse(VERSION).replaceAll("§.", ""), 100);
        else
            return TextUtil.limit(CandyTweak.WINDOW_TITLE_TEXT.parse(VERSION).replaceAll("§.", ""), 100);
    }
}
