package mod.adrenix.nostalgic.forge.mixin.flywheel;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.vanilla.ChestInstance;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.level.block.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestInstance.class)
public abstract class ChestInstanceMixin <T extends BlockEntity & LidBlockEntity> extends BlockEntityInstance<T>
{
    /**
     * Dummy constructor.
     * Suppressing warnings here is fine.
     */
    @SuppressWarnings("unchecked")
    private ChestInstanceMixin(MaterialManager materialManager, BlockEntity blockEntity)
    {
        super(materialManager, (T) blockEntity);
    }

    /* Shadows */

    @Shadow public abstract void remove();

    /* Injections */

    /**
     * Prevents modern vanilla chests from appearing when old chest tweaks are enabled.
     * Controlled by various old chest tweaks.
     */
    @Inject(method = "updateLight", at = @At("HEAD"), remap = false)
    private void NT$onUpdateLight(CallbackInfo callback)
    {
        boolean isOldChest = this.blockEntity instanceof ChestBlockEntity && ModConfig.Candy.oldChest();
        boolean isOldEnderChest = this.blockEntity instanceof EnderChestBlockEntity && ModConfig.Candy.oldEnderChest();
        boolean isOldTrappedChest = this.blockEntity instanceof TrappedChestBlockEntity && ModConfig.Candy.oldTrappedChest();

        if (isOldChest || isOldEnderChest || isOldTrappedChest)
            this.remove();
    }
}
