package mod.adrenix.nostalgic.mixin.tweak.gameplay.bugs;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.util.gameplay.ClimbableMixinHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    /* Fake Constructor */

    private LivingEntityMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Shadows */

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") @Shadow private Optional<BlockPos> lastClimbablePos;

    /* Injections */

    /**
     * Allows entities to continually climb if there is a single gap between two ladders.
     */
    @ModifyReturnValue(
        method = "onClimbable",
        at = @At("RETURN")
    )
    private boolean nt_gameplay_bugs$isGapClimbable(boolean isClimbable)
    {
        if (!GameplayTweak.OLD_LADDER_GAP.get() || isClimbable || this.isSpectator())
            return isClimbable;

        BlockState blockState = this.getBlockStateOn();
        BlockPos blockPos = this.blockPosition();
        boolean isGapPresent = ClimbableMixinHelper.isClimbable(this.level(), blockState, blockPos);

        if (isGapPresent)
            this.lastClimbablePos = Optional.of(blockPos);

        return isGapPresent;
    }
}
