package mod.adrenix.nostalgic.fabric.mixin.flywheel.candy.chest_block;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.vanilla.ChestInstance;
import mod.adrenix.nostalgic.helper.candy.block.ChestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestInstance.class)
public abstract class ChestInstanceMixin<T extends BlockEntity & LidBlockEntity> extends BlockEntityInstance<T>
{
    /* Fake Constructor */

    private ChestInstanceMixin(MaterialManager materialManager, T blockEntity)
    {
        super(materialManager, blockEntity);
    }

    /* Shadows */

    @Shadow
    public abstract void remove();

    /* Injections */

    /**
     * Prevents modern vanilla chests from appearing when the old chest tweaks are enabled.
     */
    @Inject(
        remap = false,
        method = "updateLight",
        at = @At("HEAD")
    )
    private void nt_fabric_flywheel_chest_block$onUpdateLight(CallbackInfo callback)
    {
        if (ChestHelper.isOld(this.blockState))
            this.remove();
    }
}
