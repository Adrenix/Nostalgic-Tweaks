package mod.adrenix.nostalgic.forge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.forge.event.client.GuiEvents;
import mod.adrenix.nostalgic.mixin.duck.GuiForgeOffset;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin
{
    /* Helpers */

    /**
     * Forge vanilla overlays should not run if changes are made by the mod.
     * @return Whether the overlay is blocked from running.
     */
    private static boolean isRendererBlocked()
    {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player)
            return !player.isCreative() && !player.isSpectator() && !(player.getVehicle() instanceof LivingEntity);

        return false;
    }

    /* Shadows & Unique */

    @Shadow public int rightHeight;
    @Shadow public int leftHeight;

    /* Injections */

    /**
     * Changes the starting height for overlays based on whether the experience bar is disabled.
     * Controlled by whether the overlay is blocked from rendering.
     */
    @Inject
    (
        method = "render",
        at = @At
        (
            ordinal = 0,
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"
        )
    )
    private void NT$onRender(PoseStack poseStack, float partialTick, CallbackInfo callback)
    {
        if (isRendererBlocked())
        {
            int height = ModConfig.Gameplay.disableExperienceBar() ? 32 : 39;
            this.rightHeight = height;
            this.leftHeight = height;
        }
    }

    /**
     * Prevents the Forge rendering of hearts so that the order of rendering can be controlled by the mod's HUD overlay
     * rendering event handler.
     *
     * Controlled by whether the overlay is blocked from rendering.
     */
    @Inject(method = "renderHealth", remap = false, at = @At("RETURN"))
    private void NT$onRenderHealth(int width, int height, PoseStack poseStack, CallbackInfo callback)
    {
        this.leftHeight = ((GuiForgeOffset) this).NT$getLeft();
    }

    /**
     * Changes the left/right height offsets based on whether the hunger bar is disabled.
     * Controlled by whether the overlay is blocked from rendering.
     */
    @Inject(method = "renderArmor", remap = false, at = @At("HEAD"), cancellable = true)
    private void NT$onRenderArmor(PoseStack poseStack, int width, int height, CallbackInfo callback)
    {
        if (isRendererBlocked())
        {
            if (ModConfig.Gameplay.disableHungerBar())
                this.rightHeight += 10;
            else
                this.leftHeight += 10;

            callback.cancel();
        }
    }

    /**
     * Changes the right height offset.
     * Controlled by whether the overlay is blocked from rendering.
     */
    @Inject(method = "renderFood", remap = false, at = @At("HEAD"), cancellable = true)
    private void NT$onRenderFood(int width, int height, PoseStack poseStack, CallbackInfo callback)
    {
        if (isRendererBlocked())
        {
            this.rightHeight += 10;
            callback.cancel();
        }
    }

    /**
     * Changes the left/right offset based on whether the hunger bar is disabled.
     * Controlled by whether the overlay is blocked from rendering.
     */
    @Inject(method = "renderAir", remap = false, at = @At("HEAD"), cancellable = true)
    private void NT$onRenderAir(int width, int height, PoseStack poseStack, CallbackInfo callback)
    {
        if (isRendererBlocked())
        {
            if (Minecraft.getInstance().getCameraEntity() instanceof Player player && GuiEvents.isPlayerLosingAir(player))
            {
                if (ModConfig.Gameplay.disableHungerBar())
                    this.leftHeight += 10;
                else
                    this.rightHeight += 10;
            }

            callback.cancel();
        }
    }
}
