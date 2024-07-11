package mod.adrenix.nostalgic.mixin.util.candy.lighting;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;

/**
 * This is a simple wrapper class around a chunk's {@link DataLayer} to help with old lighting. The purpose of this
 * approach is to support mods like Sodium and Embeddium without the need of a mixin into their world renderers.
 */
public class NostalgicDataLayer extends DataLayer
{
    /* Fields */

    private final DataLayer parent;
    private final LightLayer layer;
    private final ClientLevel level;

    /* Constructor */

    /**
     * Create a new mod data layer wrapper instance.
     *
     * @param parent The parent {@link DataLayer} instance to get light values from.
     * @param level  The {@link ClientLevel} instance.
     * @param layer  The {@link LightLayer} this data layer is associated with.
     */
    public NostalgicDataLayer(DataLayer parent, ClientLevel level, LightLayer layer)
    {
        this.parent = parent;
        this.level = level;
        this.layer = layer;
    }

    /* Methods */

    /**
     * @return A modified light value based on the mod's current tweak lighting context.
     */
    @Override
    public int get(int x, int y, int z)
    {
        int lightValue = this.parent.get(x, y, z);

        if (GameUtil.isOnIntegratedSeverThread())
            return lightValue;

        return getLightValue(this.layer, this.level, new BlockPos(x, y, z), lightValue);
    }

    /**
     * Get a light value based on the mod's current tweak lighting context.
     *
     * @param layer      The {@link LightLayer} this light value is for.
     * @param level      The {@link ClientLevel} instance.
     * @param blockPos   The {@link BlockPos} that light value will be applied to.
     * @param lightValue The original vanilla light value.
     * @return A new light value to use at the given block position.
     */
    public static int getLightValue(LightLayer layer, ClientLevel level, BlockPos blockPos, int lightValue)
    {
        if (CandyTweak.OLD_CLASSIC_ENGINE.get())
        {
            if (layer == LightLayer.BLOCK)
                return 13;

            return LightingMixinHelper.getClassicLight(lightValue, level, blockPos);
        }

        if (CandyTweak.ROUND_ROBIN_RELIGHT.get())
        {
            if (layer == LightLayer.BLOCK)
                return lightValue;

            return LightingMixinHelper.getCombinedLight(lightValue, level.getBrightness(LightLayer.BLOCK, blockPos));
        }

        return lightValue;
    }
}
