package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.MixinUtil;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonModel.class)
public abstract class SkeletonModelMixin <T extends Mob> extends HumanoidModel<T>
{
    /* Dummy Constructor */

    private SkeletonModelMixin(ModelPart modelPart) { super(modelPart); }

    /**
     * Disables skeleton arm positioning when the mob becomes aggressive.
     * Controlled by the old skeleton arms tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "prepareMobModel(Lnet/minecraft/world/entity/Mob;FFF)V",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;getMainArm()Lnet/minecraft/world/entity/HumanoidArm;"
        )
    )
    private void NT$onPrepareAggressiveMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick, CallbackInfo callback)
    {
        if (MixinConfig.Animation.oldSkeletonArms())
            callback.cancel();
    }

    /**
     * Prevents the modern animation for default skeleton arm position and firing.
     * Controlled by the old skeleton arms tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "setupAnim(Lnet/minecraft/world/entity/Mob;FFFFF)V",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private void NT$onSetupSkeletonArms(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        if (!MixinConfig.Animation.oldSkeletonArms())
            return;

        MixinUtil.Animation.setStaticArms(this.rightArm, this.leftArm);
        AnimationUtils.bobArms(this.rightArm, this.leftArm, ageInTicks);

        callback.cancel();
    }
}
