package mod.adrenix.nostalgic.helper.gameplay;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * This utility class is used by both the client and server.
 */
public abstract class MobAiHelper
{
    /**
     * Strafe the given mob counter-clockwise around the given target.
     *
     * @param mob    The {@link Mob} that is strafing.
     * @param target The {@link LivingEntity} target the mob is to strafe around.
     */
    public static void strafeAroundTarget(Mob mob, LivingEntity target)
    {
        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        float yRotO = mob.getYRot();

        mob.setYRot((float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F);
        float angle = (yRotO - mob.getYRot() + 90.0F) * (float) Math.PI / 180.0F;

        mob.setXxa(-Mth.sin(angle) * mob.getSpeed() * 1.0F);
        mob.setZza(Mth.cos(angle) * mob.getSpeed() * 1.0F);

        mob.getLookControl().setLookAt(target);
    }
}
