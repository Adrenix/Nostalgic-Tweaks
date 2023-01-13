package mod.adrenix.nostalgic.mixin.common.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends TieredItem
{
    /* Dummy Constructor */

    private DiggerItemMixin(Tier tier, Properties properties) { super(tier, properties); }

    /* Shadows */

    @Shadow @Final @Mutable private Multimap<Attribute, AttributeModifier> defaultModifiers;
    @Unique private Multimap<Attribute, AttributeModifier> NT$defaultModifiers;
    @Unique private boolean NT$isModifierAllowed;

    /* Injections */

    /**
     * Creates an old damage modifier map.
     * Depending on the old damage values tweak, the returned map will switch between the vanilla and the mod's.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void NT$onConstructItem(float baseline, float speed, Tier tier, TagKey<Block> tagKey, Properties properties, CallbackInfo callback)
    {
        HashMultimap<Attribute, AttributeModifier> builder = HashMultimap.create();

        for (Map.Entry<Attribute, AttributeModifier> entry : this.defaultModifiers.entries())
        {
            if (!entry.getKey().equals(Attributes.ATTACK_DAMAGE))
                builder.put(entry.getKey(), entry.getValue());
        }

        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", ItemServerUtil.getOldDamage(this), AttributeModifier.Operation.ADDITION));

        this.NT$defaultModifiers = builder;
        this.NT$isModifierAllowed = ItemServerUtil.isVanillaTiered(this);
    }

    /**
     * Changes the item's default attribute modifier map.
     * Controlled by the old damage values tweak.
     */
    @Inject(method = "getDefaultAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void NT$onGetDefaultAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback)
    {
        if (ModConfig.Gameplay.oldDamageValues() && this.NT$isModifierAllowed && slot == EquipmentSlot.MAINHAND)
            callback.setReturnValue(this.NT$defaultModifiers);
    }
}
