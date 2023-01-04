package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.api.ClientEventFactory;
import mod.adrenix.nostalgic.api.event.HudEvent;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.duck.GuiForgeOffset;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class GuiMixin extends GuiComponent implements GuiForgeOffset
{
    /* Trackers */

    @Unique private int NT$leftForgeOffset = 0;
    @Unique private int NT$leftHeight = 39;
    @Unique private int NT$rightHeight = 39;
    @Unique private int NT$heartIndex = -1;
    @Unique private int NT$heartRow = 0;

    /* Shadows */

    @Shadow protected abstract LivingEntity getPlayerVehicleWithHealth();
    @Shadow protected abstract Player getCameraPlayer();
    @Shadow private int screenWidth;
    @Shadow private int screenHeight;

    /* Implementation */

    /**
     * Get the current height offset for the left side of the HUD.
     * @return Current left height offset.
     */
    @Override
    public int NT$getLeft() { return this.NT$leftForgeOffset; }

    /* Injections */

    /**
     * Disables the rendering of the selected item name above the hotbar.
     * Controlled by the old selected item name tweak.
     */
    @Inject(method = "renderSelectedItemName", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onRenderSelectedItemName(PoseStack poseStack, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldNoSelectedItemName())
            callback.cancel();
    }

    /**
     * Removes the chat formatting of the selected item above the hotbar.
     * Controlled by the old plain selected item name tweak.
     */
    @Inject
    (
        method = "renderSelectedItemName",
        locals = LocalCapture.CAPTURE_FAILSOFT,
        at = @At
        (
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"
        )
    )
    private void NT$onDrawSelectedItemName(PoseStack poseStack, CallbackInfo callback, MutableComponent mutableComponent)
    {
        if (ModConfig.Candy.oldPlainSelectedItemName())
            mutableComponent.withStyle(ChatFormatting.RESET);
    }

    /**
     * Prevents the axis crosshair from overriding the default crosshair.
     * Controlled by the old debug screen tweak.
     */
    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;renderDebug:Z", opcode = Opcodes.GETFIELD))
    private boolean NT$onRenderDebugCrosshair(Options instance)
    {
        if (ModConfig.Candy.getDebugScreen().equals(TweakVersion.Generic.MODERN))
            return instance.renderDebug;

        return false;
    }

    /**
     * Disables the rendering of the "ready to attack" indicator status.
     * Controlled by the disabled cooldown tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "renderCrosshair",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"
        )
    )
    private void NT$onRenderAttackIndicator(PoseStack poseStack, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableCooldown())
            callback.cancel();
    }

    /*
        Hud Rendering

        Since Fabric does not have a HUD overlay event system, a different approach must be taken when rendering the
        changes for the in-game HUD. The blit rendering for armor, food, and air bubbles are cancelled when the player
        is not riding a vehicle. This is needed so the order of rendering can be changed. The food icons are rendered
        first since the left/right vertical offsets need updated based on whether the food icons are rendered. Next, the
        hearts rendered since there can be additional row of hearts above the first row. The armor and air bubble icons
        are rendered last since their vertical positions are dependent on where the heart and food icons are. All icon
        vertical offsets are shifted based on whether the experience bar is rendered.

        Some mods, such as the Apple Skin mod, will receive support due to the simplicity of implementing support. Other
        mods may want to use the Nostalgic HUD API to get the starting x/y positions for rows of icons if they want to
        support Nostalgic Tweaks HUD modifications.
    */

    /**
     * @return A starting y-position based on whether the experience bar is enabled.
     */
    private static int getStartHeight()
    {
        return ModConfig.Gameplay.disableExperienceBar() ? 32 : 39;
    }

    /**
     * Resets the left/right offset trackers when the in-game HUD is rendered.
     */
    @Inject(method = "renderPlayerHealth", at = @At("HEAD"))
    private void NT$onRenderHud(PoseStack poseStack, CallbackInfo callback)
    {
        int height = GuiMixin.getStartHeight();
        this.NT$leftHeight = height;
        this.NT$rightHeight = height;
    }

    /**
     * Resets the heart icon index tracker when the HUD heart icons are rendered. Also updates the left-height offset
     * tracker before the hearts are rendered.
     *
     * Not controlled by any tweaks.
     */
    @Inject(method = "renderHearts", at = @At("HEAD"))
    private void NT$onRenderHearts(PoseStack poseStack, Player player, int x, int y, int height, int regen, float healthMax, int health, int healthLast, int absorb, boolean highlight, CallbackInfo callback)
    {
        this.NT$heartIndex = -1;
        this.NT$heartRow = 0;

        int healthRows = Mth.ceil((healthMax + (float) absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.NT$leftForgeOffset = GuiMixin.getStartHeight() + (healthRows * rowHeight);
        this.NT$leftHeight += healthRows * rowHeight;

        if (rowHeight != 10)
        {
            this.NT$leftForgeOffset += 10 - rowHeight;
            this.NT$leftHeight += 10 - rowHeight;
        }
    }

    /**
     * Updates the heart icon index tracker when a heart container is rendered.
     * Not controlled by any tweaks.
     */
    @Inject
    (
        method = "renderHearts",
        at = @At
        (
            ordinal = 0,
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V"
        )
    )
    private void NT$onRenderHeartContainer(PoseStack poseStack, Player player, int x, int y, int height, int regen, float healthMax, int health, int healthLast, int absorb, boolean highlight, CallbackInfo callback)
    {
        ++this.NT$heartIndex;
    }

    /**
     * Moves the position of the player's heart bar.
     * Controlled by various gameplay tweaks.
     */
    @Redirect(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    private void NT$onRenderHeart(Gui instance, PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (this.NT$heartIndex >= 10)
        {
            this.NT$heartIndex = 0;
            this.NT$heartRow += 1;
        }

        if (this.getPlayerVehicleWithHealth() != null)
        {
            instance.blit(poseStack, x, y, uOffset, vOffset, uWidth, vHeight);

            GuiUtil.heartY = y;
            return;
        }

        int startY = ModConfig.Gameplay.disableExperienceBar() && NostalgicTweaks.isFabric() ? y + 7 : y;

        HudEvent event = ClientEventFactory.RENDER_HEART.create(x, startY, this.NT$heartIndex, this.NT$heartRow, poseStack);
        event.emit();

        GuiUtil.heartY = event.getY();

        if (!event.isCanceled())
            instance.blit(poseStack, event.getX(), event.getY(), uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Prevents the rendering of vanilla armor icons when not riding a vehicle.
     * Not controlled by any tweaks.
     */
    @Redirect
    (
        method = "renderPlayerHealth",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"),
        slice = @Slice
        (
            from = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"),
            to = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")
        )
    )
    private void NT$onBlitArmor(Gui gui, PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (this.getPlayerVehicleWithHealth() != null)
            gui.blit(poseStack, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Prevents the rendering of vanilla food icons when not riding a vehicle.
     * Not controlled by any tweaks.
     */
    @Redirect
    (
        method = "renderPlayerHealth",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"),
        slice = @Slice
        (
            from = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"),
            to = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")
        )
    )
    private void NT$onBlitFood(Gui gui, PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (this.getPlayerVehicleWithHealth() != null)
            gui.blit(poseStack, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Prevents the rendering of vanilla air bubble icons when not riding a vehicle.
     * Not controlled by any tweaks.
     */
    @Redirect
    (
        method = "renderPlayerHealth",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"),
        slice = @Slice
        (
            from = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V")
        )
    )
    private void NT$onBlitAir(Gui gui, PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (this.getPlayerVehicleWithHealth() != null)
            gui.blit(poseStack, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Renders the food texture icon.
     * Controlled by various HUD tweaks.
     */
    @Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"))
    private void NT$onRenderFood(PoseStack poseStack, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableHungerBar() || this.getPlayerVehicleWithHealth() != null)
            return;

        GuiUtil.renderFood((Gui) (Object) this, poseStack, this.getCameraPlayer(), this.screenWidth, this.screenHeight, this.NT$rightHeight);

        this.NT$rightHeight += 10;
    }

    /**
     * Renders the armor texture icon.
     * Controlled by various HUD tweaks.
     */
    @Inject
    (
        method = "renderPlayerHealth",
        at = @At
        (
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"
        )
    )
    private void NT$onRenderArmor(PoseStack poseStack, CallbackInfo callback)
    {
        if (this.getPlayerVehicleWithHealth() != null)
            return;

        GuiUtil.renderArmor
        (
            (Gui) (Object) this,
            poseStack,
            this.getCameraPlayer(),
            this.screenWidth,
            this.screenHeight,
            this.NT$leftHeight,
            this.NT$rightHeight
        );

        if (ModConfig.Gameplay.disableHungerBar())
            this.NT$rightHeight += 10;
        else
            this.NT$leftHeight += 10;
    }

    /**
     * Boolean supplier that is used by the common GUI utility to check if the player is losing air.
     * @param player A player instance.
     * @return Whether the player is losing air.
     */
    private static boolean isPlayerLosingAir(Player player)
    {
        return player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < 300;
    }

    /**
     * Renders the air bubble texture icon.
     * Controlled by various HUD tweaks.
     */
    @Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
    private void NT$onRenderAir(PoseStack poseStack, CallbackInfo callback)
    {
        if (this.getPlayerVehicleWithHealth() != null)
            return;

        GuiUtil.renderAir
        (
            GuiMixin::isPlayerLosingAir,
            (Gui) (Object) this,
            poseStack,
            this.getCameraPlayer(),
            this.screenWidth,
            this.screenHeight,
            this.NT$leftHeight,
            this.NT$rightHeight
        );

        if (isPlayerLosingAir(this.getCameraPlayer()))
        {
            if (ModConfig.Gameplay.disableHungerBar())
                this.NT$leftHeight += 10;
            else
                this.NT$rightHeight += 10;
        }
    }
}
