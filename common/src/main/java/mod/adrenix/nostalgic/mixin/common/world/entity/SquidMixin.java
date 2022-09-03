package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Squid.class)
public abstract class SquidMixin extends WaterAnimal
{
    /* Dummy Constructor */

    private SquidMixin(EntityType<? extends WaterAnimal> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injection Overrides */

    /**
     * Brings back the old bug that allowed players to milk squids.
     * Controlled by the old squid milking tweak.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        ItemStack holding = player.getItemInHand(hand);

        if (ModConfig.Gameplay.oldSquidMilk() && holding.is(Items.BUCKET))
        {
            player.setItemInHand(hand, ItemUtils.createFilledResult(holding, player, Items.MILK_BUCKET.getDefaultInstance()));
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        return super.mobInteract(player, hand);
    }
}
