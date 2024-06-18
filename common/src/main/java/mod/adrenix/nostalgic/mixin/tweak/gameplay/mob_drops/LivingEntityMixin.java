package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_drops;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.mixin.util.gameplay.MobLootMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Modifies the loot table to get items from for specific entities.
     */
    @ModifyExpressionValue(
        method = "dropFromLootTable",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"
        )
    )
    private LootTable nt_mob_drops$modifyDropLootTable(LootTable lootTable)
    {
        return MobLootMixinHelper.getTable((Entity) (Object) this, lootTable);
    }
}
