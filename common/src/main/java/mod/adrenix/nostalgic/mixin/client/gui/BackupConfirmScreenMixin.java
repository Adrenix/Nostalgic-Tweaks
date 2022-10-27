package mod.adrenix.nostalgic.mixin.client.gui;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackupConfirmScreen.class)
public abstract class BackupConfirmScreenMixin
{
    /* Shadows */

    @Shadow private Checkbox eraseCache;

    /**
     * Automatically checks the clear cache checkbox. This is a quality of life feature for tweaks that makes changes
     * to the world. The cache needs to be cleared if existing chunks are to be successfully updated with new block
     * data.
     *
     * This mixin is not controlled by any tweaks.
     */
    @Inject(method = "init", at = @At("RETURN"))
    private void NT$onInit(CallbackInfo callback)
    {
        if (!this.eraseCache.selected())
            this.eraseCache.onPress();
    }
}
