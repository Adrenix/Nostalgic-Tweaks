package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BodyRotationControl.class)
public interface BodyRotationControlAccess
{
    @Invoker("rotateBodyIfNecessary")
    void nt$rotateBodyIfNecessary();

    @Invoker("rotateHeadIfNecessary")
    void nt$rotateHeadIfNecessary();

    @Invoker("isMoving")
    boolean nt$isMoving();

    @Invoker("notCarryingMobPassengers")
    boolean nt$notCarryingMobPassengers();
}
