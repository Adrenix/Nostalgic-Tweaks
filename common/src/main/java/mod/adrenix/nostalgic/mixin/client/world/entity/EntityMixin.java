package mod.adrenix.nostalgic.mixin.client.world.entity;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    /* Shadows */

    @Shadow public Level level;
    @Shadow public abstract int getId();

    /* Injections */

    /**
     * Sets the entity's ID as its custom name.
     * Controlled by the debug entity id tweak.
     */
    @Inject(method = "getCustomName", at = @At("HEAD"), cancellable = true)
    private void NT$onGetCustomName(CallbackInfoReturnable<Component> callback)
    {
        if (ModConfig.Candy.debugEntityId() && NostalgicTweaks.isNetworkVerified() && Minecraft.getInstance().options.renderDebug)
            callback.setReturnValue(Component.literal(Integer.toString(this.getId())));
    }

    /**
     * Sets the entity's custom name to visible when the debug screen is rendering.
     * Controlled by the debug entity id tweak.
     */
    @Inject(method = "isCustomNameVisible", at = @At("HEAD"), cancellable = true)
    private void NT$onIsCustomNameVisible(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Candy.debugEntityId() && NostalgicTweaks.isNetworkVerified() && Minecraft.getInstance().options.renderDebug)
            callback.setReturnValue(true);
    }
}
