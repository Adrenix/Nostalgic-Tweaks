package mod.adrenix.nostalgic.mixin.client.model;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonEntityModel.class)
public abstract class SkeletonModelMixin <T extends MobEntity> extends BipedEntityModel<T>
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
        if (ModConfig.Animation.oldSkeletonArms())
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
        if (!ModConfig.Animation.oldSkeletonArms())
            return;

        ModClientUtil.Animation.setStaticArms(this.rightArm, this.leftArm);
        CrossbowPosing.swingArms(this.rightArm, this.leftArm, ageInTicks);

        callback.cancel();
    }
}
