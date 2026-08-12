package mod.adrenix.nostalgic.fabric.mixin.tweak.gameplay.stamina_sprint;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.api.NostalgicAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    /**
     * Fabric API does not seem to provide a way to add attributes to entities, so this mixin appends our attribute to
     * the player when vanilla attributes are created.
     */
    @ModifyReturnValue(
        method = "createAttributes",
        at = @At("RETURN")
    )
    private static AttributeSupplier.Builder nt_fabric_stamina_sprint$modifyPlayerAttributes(AttributeSupplier.Builder builder)
    {
        return builder.add(NostalgicAttributes.MAX_STAMINA);
    }
}
