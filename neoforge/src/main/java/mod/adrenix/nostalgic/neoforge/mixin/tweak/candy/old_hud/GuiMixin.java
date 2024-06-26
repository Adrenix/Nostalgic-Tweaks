package mod.adrenix.nostalgic.neoforge.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.mixin.util.candy.hud.HudMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /* Shadows */

    @Shadow public int leftHeight;
    @Shadow public int rightHeight;

    /* Injections */

    /**
     * Prevents rendering of the modern armor sprites if the hunger bar is hidden.
     */
    @WrapWithCondition(
        method = "renderArmorLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderArmor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIII)V"
        )
    )
    private boolean nt_neoforge_old_hud$shouldRenderModernArmor(GuiGraphics graphics, Player player, int y, int heartRows, int height, int x)
    {
        return !CandyTweak.HIDE_HUNGER_BAR.get();
    }

    /**
     * Renders the old armor sprites on the right side of the heads-up display.
     */
    @Inject(
        method = "renderArmorLevel",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderArmor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIII)V"
        )
    )
    private void nt_neoforge_old_hud$renderOldArmor(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
        {
            HudMixinHelper.renderArmor(graphics, this.rightHeight);

            if (NullableResult.getOrElse(Minecraft.getInstance().player, 0, LocalPlayer::getArmorValue) > 0)
            {
                this.leftHeight -= 10;
                this.rightHeight += 10;
            }
        }
    }

    /**
     * Prevents rendering of the modern air sprites if the hunger bar is hidden.
     */
    @WrapWithCondition(
        method = "renderAirLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private boolean nt_neoforge_old_hud$shouldRenderModernAir(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !CandyTweak.HIDE_HUNGER_BAR.get();
    }

    /**
     * Renders the old air sprites on the left side of the heads-up display above the hearts.
     */
    @Inject(
        method = "renderAirLevel",
        at = @At(
            shift = At.Shift.BEFORE,
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;ceil(D)I"
        )
    )
    private void nt_neoforge_old_hud$renderOldAir(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
        {
            HudMixinHelper.renderAir(graphics, this.leftHeight);

            this.leftHeight += 10;
            this.rightHeight -= 10;
        }
    }
}
