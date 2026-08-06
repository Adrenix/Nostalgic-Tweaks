package mod.adrenix.nostalgic.neoforge.event;

import mod.adrenix.nostalgic.helper.candy.block.TorchHelper;
import mod.adrenix.nostalgic.helper.candy.block.cross.CrossBlock;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import org.embeddedt.embeddium.api.BlockRendererRegistry;
import org.embeddedt.embeddium.api.render.chunk.RenderSectionDistanceFilter;
import org.embeddedt.embeddium.api.render.chunk.RenderSectionDistanceFilterEvent;

public abstract class EmbeddiumHandler
{
    /**
     * Implements old square border support for Embeddium. This modifies the distance filter to mimic the old chessboard
     * chunk range.
     *
     * @param event The {@link RenderSectionDistanceFilterEvent} instance.
     */
    @SubscribeEvent
    public static void onRenderSectionDistanceEvent(RenderSectionDistanceFilterEvent event)
    {
        final RenderSectionDistanceFilter filter = event.getFilter();

        event.setFilter((dx, dy, dz, maxDistance) -> {
            if (CandyTweak.OLD_SQUARE_BORDER.get())
                return Math.max(Math.abs(dx), Math.abs(dz)) <= maxDistance + 1 && Math.abs(dy) < maxDistance;
            else
                return filter.isWithinDistance(dx, dy, dz, maxDistance);
        });
    }

    /**
     * Initialize the Embeddium handler.
     */
    public static void init()
    {
        BlockRendererRegistry.instance().registerRenderPopulator((resultList, context) -> {
            if (TorchHelper.isNotLikeTorch(context.state()) && !(context.state().getBlock() instanceof FenceBlock))
                return;

            resultList.add((ctx, random, consumer) -> {
                BlockPos origin = new BlockPos((int) ctx.origin().x(), (int) ctx.origin().y(), (int) ctx.origin().z());
                BlockState crossState = CrossBlock.getState(ctx.localSlice(), ctx.state(), ctx.pos());

                if (ctx.state() != crossState)
                    ctx.update(ctx.pos(), origin, crossState, ctx.model(), ctx.seed(), ctx.modelData(), ctx.renderLayer());

                if (TorchHelper.isLikeTorch(ctx.state()))
                {
                    TorchHelper.writeVertices(ctx.stack(), ctx.world(), consumer, ctx.model(), ctx.state(), ctx.pos(), random);

                    return BlockRendererRegistry.RenderResult.OVERRIDE;
                }

                return BlockRendererRegistry.RenderResult.PASS;
            });
        });
    }
}
