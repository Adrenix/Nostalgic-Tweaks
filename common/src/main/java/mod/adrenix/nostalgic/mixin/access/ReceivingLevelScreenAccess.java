package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BooleanSupplier;

@Mixin(ReceivingLevelScreen.class)
public interface ReceivingLevelScreenAccess
{
    @Accessor("createdAt")
    long nt$getCreatedAt();

    @Accessor("levelReceived")
    BooleanSupplier nt$getLevelReceived();
}
