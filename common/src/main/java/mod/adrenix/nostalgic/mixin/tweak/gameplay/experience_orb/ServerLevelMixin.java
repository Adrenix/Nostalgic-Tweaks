package mod.adrenix.nostalgic.mixin.tweak.gameplay.experience_orb;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{
    /**
     * Prevents the server level from spawning experience orbs.
     */
    @SuppressWarnings("MixinExtrasOperationParameters")
    @WrapOperation(
        method = "addEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addNewEntity(Lnet/minecraft/world/level/entity/EntityAccess;)Z"
        )
    )
    private <T extends EntityAccess> boolean nt_experience_orb$shouldAddExperienceEntity(PersistentEntitySectionManager<T> manager, T entity, Operation<Boolean> operation)
    {
        if (GameplayTweak.DISABLE_ORB_SPAWN.get() && entity instanceof ExperienceOrb)
            return false;

        return operation.call(manager, entity);
    }
}
