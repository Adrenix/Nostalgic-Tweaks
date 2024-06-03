package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.mixin.util.candy.hud.HudElement;
import mod.adrenix.nostalgic.mixin.util.candy.hud.HudMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /**
     * Shifts all heads-up display elements if the experience bar is hidden.
     */
    @Inject(
        method = "render",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void nt_fabric_old_hud$shiftHudElements(GuiGraphics graphics, float partialTick, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            HudMixinHelper.begin(graphics);

        if (CandyTweak.HIDE_EXPERIENCE_BAR.get())
            HudMixinHelper.apply(graphics, HudElement.EXPERIENCE_BAR);
    }

    /**
     * Prevents rendering of the experience bar on the heads-up display.
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"
        )
    )
    private boolean nt_fabric_old_hud$shouldExperienceBarRender(boolean hasExperience)
    {
        if (CandyTweak.HIDE_EXPERIENCE_BAR.get())
            return false;

        return hasExperience;
    }

    /**
     * Shifts the stack in preparation of rendering the old armor sprites on the heads-up display.
     */
    @Inject(
        method = "renderPlayerHealth",
        at = @At(
            shift = At.Shift.AFTER,
            value = "CONSTANT",
            args = "stringValue=armor"
        )
    )
    private void nt_fabric_old_hud$modifyArmorElement(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudMixinHelper.apply(graphics, HudElement.ARMOR);
    }

    /**
     * Prevents the rendering of the modern armor sprites.
     */
    @WrapWithCondition(
        method = "renderPlayerHealth",
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=armor"
            ),
            to = @At(
                value = "CONSTANT",
                args = "stringValue=health"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private boolean nt_fabric_old_hud$shouldArmorSpriteRender(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !CandyTweak.HIDE_HUNGER_BAR.get();
    }

    /**
     * Pops translations set by the old armor sprites.
     */
    @Inject(
        method = "renderPlayerHealth",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "CONSTANT",
            args = "stringValue=health"
        )
    )
    private void nt_fabric_old_hud$popPushArmorOffsets(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudMixinHelper.pop(graphics);
    }

    /**
     * Prevents other mod food-related elements from rendering.
     */
    @Inject(
        method = "renderPlayerHealth",
        at = @At(
            shift = At.Shift.AFTER,
            value = "CONSTANT",
            args = "stringValue=food"
        )
    )
    private void nt_fabric_old_hud$preHideFoodRelatedElements(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudMixinHelper.apply(graphics, HudElement.FOOD);
    }

    /**
     * Prevents the rendering of the modern food sprites.
     */
    @WrapWithCondition(
        method = "renderPlayerHealth",
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=food"
            ),
            to = @At(
                value = "CONSTANT",
                args = "stringValue=air"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private boolean nt_fabric_old_hud$shouldFoodSpriteRender(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !CandyTweak.HIDE_HUNGER_BAR.get();
    }

    /**
     * Restores the visibility of the current shader after rendering food-related elements.
     */
    @Inject(
        method = "renderPlayerHealth",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "CONSTANT",
            args = "stringValue=air"
        )
    )
    private void nt_fabric_old_hud$postHideFoodRelatedElements(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudMixinHelper.pop(graphics);
    }

    /**
     * Shifts the stack in preparation of rendering the old air bubbles on the heads-up display.
     */
    @Inject(
        method = "renderPlayerHealth",
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=air"
            )
        ),
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;getVisibleVehicleHeartRows(I)I"
        )
    )
    private void nt_fabric_old_hud$modifyAirElement(GuiGraphics graphics, CallbackInfo callback, @Local(ordinal = 0) int offsetHeight)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudMixinHelper.apply(graphics, HudElement.AIR);
    }

    /**
     * Prevents the rendering of the modern air bubble sprites.
     */
    @WrapWithCondition(
        method = "renderPlayerHealth",
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=air"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private boolean nt_fabric_old_hud$shouldAirSpriteRender(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !CandyTweak.HIDE_HUNGER_BAR.get();
    }

    /**
     * Pops translations set by the old air bubble sprites.
     */
    @Inject(
        method = "renderPlayerHealth",
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=air"
            )
        ),
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
        )
    )
    private void nt_fabric_old_hud$popPushAirOffsets(GuiGraphics graphics, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudMixinHelper.pop(graphics);
    }

    /**
     * Shifts the vehicle health upwards if the old armor sprites are rendered on the right side of the heads-up
     * display.
     */
    @Inject(
        method = "render",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void nt_fabric_old_hud$shiftVehicleHealth(GuiGraphics graphics, float partialTick, CallbackInfo callback)
    {
        if (CandyTweak.HIDE_HUNGER_BAR.get())
            HudMixinHelper.apply(graphics, HudElement.VEHICLE_HEALTH);
    }

    /**
     * Tears down graphical changes made by the mod after the vehicle health has been rendered. Any elements rendered
     * after this point will not receive previous offsets defined by the mod.
     */
    @Inject(
        method = "render",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void nt_fabric_old_hud$endHudShift(GuiGraphics graphics, float partialTick, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            HudMixinHelper.end(graphics);
    }
}
