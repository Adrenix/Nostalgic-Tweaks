package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(DebugScreenOverlay.class)
public interface DebugScreenOverlayAccess
{
    @Accessor("block")
    HitResult nt$getBlockHitResult();

    @Accessor("liquid")
    HitResult nt$getLiquidHitResult();

    @Invoker("getPropertyValueString")
    String nt$getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> entry);

    @Invoker("printBiome")
    static String nt$printBiome(Holder<Biome> biomeHolder)
    {
        throw new AssertionError();
    }
}
