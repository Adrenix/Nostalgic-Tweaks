package mod.adrenix.nostalgic.mixin.tweak.gameplay.stamina_sprint;

import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaCodec;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaData;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements StaminaHolder
{
    @Unique private StaminaData nt_stamina_sprint$data = StaminaData.create();

    @Override
    public @NotNull StaminaData nt$getStaminaData()
    {
        return this.nt_stamina_sprint$data;
    }

    @Override
    public void nt$setStaminaData(@NotNull StaminaData data)
    {
        this.nt_stamina_sprint$data = data;
    }

    /**
     * Saves the player's stamina data to their NBT. Saving will happen regardless of tweak context so stamina state is
     * consistent.
     */
    @Inject(
        method = "addAdditionalSaveData",
        at = @At("TAIL")
    )
    private void nt_stamina_sprint$onFinishAddAdditionalSaveData(CompoundTag compound, CallbackInfo callback)
    {
        StaminaCodec.save((Player) (Object) this, compound);
    }

    /**
     * Read the player's stamina data from their NBT.
     */
    @Inject(
        method = "readAdditionalSaveData",
        at = @At("TAIL")
    )
    private void nt_stamina_sprint$onFinishReadAdditionalSaveData(CompoundTag compound, CallbackInfo callback)
    {
        StaminaCodec.read((Player) (Object) this, compound);
    }
}
