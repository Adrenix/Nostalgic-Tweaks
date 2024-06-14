package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockMixinHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public MultiPlayerGameMode gameMode;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Final public Options options;
    @Shadow protected int missTime;

    /* Injections */

    /**
     * Resets the miss time tracker when starting an attack.
     */
    @Inject(
        method = "startAttack",
        at = @At("HEAD")
    )
    private void nt_combat_player$onStartAttack(CallbackInfoReturnable<Boolean> callback)
    {
        if (GameplayTweak.DISABLE_MISS_TIMER.get())
            this.missTime = 0;
    }

    /**
     * Resets the miss time tracker when continuing an attack.
     */
    @Inject(
        method = "continueAttack",
        at = @At("HEAD")
    )
    private void nt_combat_player$onContinueAttack(boolean leftClick, CallbackInfo callback)
    {
        if (GameplayTweak.DISABLE_MISS_TIMER.get())
            this.missTime = 0;
    }

    /**
     * Prevents the player from swinging their interaction hand on the client when sword blocking.
     */
    @WrapWithCondition(
        method = "startUseItem",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isItemEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"
            )
        ),
        at = @At(
            ordinal = 2,
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_combat_player$shouldSwingOnSwordBlock(LocalPlayer player, InteractionHand hand)
    {
        return !SwordBlockMixinHelper.isBlocking(player);
    }

    /**
     * Prevents the reequipped animation after sword blocking.
     */
    @WrapWithCondition(
        method = "startUseItem",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isItemEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"
            )
        ),
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;itemUsed(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_combat_player$shouldSetItemAsUsed(ItemInHandRenderer itemInHandRenderer, InteractionHand hand)
    {
        if (this.player == null)
            return true;

        return !SwordBlockMixinHelper.isBlocking(this.player);
    }

    /**
     * Allows the player to attack while sword blocking.
     */
    @ModifyExpressionValue(
        method = "handleKeybinds",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/Minecraft;openChatScreen(Ljava/lang/String;)V"
            )
        ),
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        )
    )
    private boolean nt_combat_player$shouldAttackWhenSwordBlocking(boolean isUsingItem)
    {
        if (this.player == null || this.gameMode == null)
            return isUsingItem;

        if (GameplayTweak.ATTACK_WHILE_SWORD_BLOCKING.get() && SwordBlockMixinHelper.isBlocking(this.player))
        {
            if (!this.options.keyUse.isDown())
                this.gameMode.releaseUsingItem(this.player);

            return false;
        }

        return isUsingItem;
    }

    /**
     * Allows the player to continue attacking while sword blocking.
     */
    @ModifyExpressionValue(
        method = "continueAttack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        )
    )
    private boolean nt_combat_player$shouldContinueAttackWhenSwordBlocking(boolean isUsingItem)
    {
        if (this.player == null)
            return isUsingItem;

        if (GameplayTweak.ATTACK_WHILE_SWORD_BLOCKING.get() && SwordBlockMixinHelper.isBlocking(this.player))
            return false;

        return isUsingItem;
    }
}
