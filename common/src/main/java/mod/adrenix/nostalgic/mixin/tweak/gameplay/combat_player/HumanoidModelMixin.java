package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockMixinHelper;
import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin
{
    /* Shadows */

    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightArm;

    /* Injections */

    /**
     * Applies the third-person view sword blocking animation if applicable.
     */
    @Inject(
        method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V"
        )
    )
    private <T extends LivingEntity> void nt_combat_player$onSetupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        if (entity instanceof Player player && SwordBlockMixinHelper.isBlocking(player))
            SwordBlockRenderer.applyThirdPerson(player, this.leftArm, this.rightArm);
    }
}
