package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.network.packet.PacketS2CHurtDirection;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * All mixins within this class are injected into both client and server.
 * Do not class load any vanilla client code here.
 * @see mod.adrenix.nostalgic.mixin.client.world.entity.LivingEntityMixin
 * @see mod.adrenix.nostalgic.mixin.server.LivingEntityMixin
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    /* Dummy Constructor */

    private LivingEntityMixin(EntityType<?> entity, Level level)
    {
        super(entity, level);
    }

    /* Shadows */

    @Shadow public float hurtDir;

    /* Injections */

    /**
     * Sends a last hurt source packet to the client so the damage tilt animation can be applied.
     * Controlled by the old damage tilt tweak.
     */
    @Inject(method = "hurt", at = @At("TAIL"))
    private void NT$onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback)
    {
        LivingEntity living = (LivingEntity) (Object) this;

        if (ModConfig.Animation.oldDirectionTilt() && living instanceof ServerPlayer player)
            PacketUtil.sendToPlayer(player, new PacketS2CHurtDirection(this.hurtDir));
    }

    /**
     * Prevents living entities from playing the food consumption sound.
     * Controlled by the old hunger tweak.
     */
    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void NT$onEat(Level instance, Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch)
    {
        if (ModConfig.Gameplay.instantEat())
            return;
        instance.playSound(player, x, y, z, sound, category, volume, pitch);
    }

    /**
     * Prevents the spawning of food consumption particles and eating sounds.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "triggerItemUseEffects", at = @At("HEAD"), cancellable = true)
    private void NT$onAddEatEffect(ItemStack itemStack, int count, CallbackInfo callback)
    {
        if (ModConfig.Gameplay.instantEat() && itemStack.getUseAnimation() == UseAnim.EAT)
            callback.cancel();
    }

    /**
     * Brings back the old backwards walking animation.
     * Controlled by the old backwards walk tweak.
     */
    @ModifyConstant
    (
        method = "tick",
        slice = @Slice(to = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;attackAnim:F")),
        constant = @Constant(floatValue = 180.0F)
    )
    private float NT$onBackwardsRotation(float vanilla)
    {
        return ModConfig.Animation.oldBackwardsWalking() ? 0.0F : vanilla;
    }

    /**
     * Immediately refills the player's air supply when they go above water.
     * Controlled by the instant air tweak.
     */
    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void NT$onIncreaseAirSupply(int currentAir, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.instantAir())
            callback.setReturnValue(this.getMaxAirSupply());
    }

    /**
     * Makes zombie flesh not as overpowered by swapping the hunger effect with poison.
     * Controlled by the old hunger tweak.
     */
    @Inject
    (
        cancellable = true,
        method = "addEatEffect",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodProperties;getEffects()Ljava/util/List;"
        )
    )
    private void NT$onAddEatEffect(ItemStack itemStack, Level level, LivingEntity entity, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.disableHunger())
            return;

        Item item = itemStack.getItem();
        boolean isRotten = item.equals(Items.ROTTEN_FLESH);
        boolean isGolden = item.equals(Items.GOLDEN_APPLE);
        boolean isEffectOverride = isRotten || isGolden;

        if (isRotten)
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));

        if (isEffectOverride)
            callback.cancel();
    }

    /**
     * Ensures that any food loot dropped doesn't get overridden by the old food stacking tweaking.
     * Not controlled by any tweak.
     */

    @Inject(method = "dropFromLootTable", at = @At("HEAD"))
    private void NT$onStartDropFromLootTable(DamageSource damageSource, boolean hitByPlayer, CallbackInfo callback)
    {
        ItemServerUtil.isDroppingLoot = true;
    }

    @Inject(method = "dropFromLootTable", at = @At("TAIL"))
    private void NT$onEndDropFromLootTable(DamageSource damageSource, boolean hitByPlayer, CallbackInfo callback)
    {
        ItemServerUtil.isDroppingLoot = false;
    }

    /**
     * Changes the loot dropped by certain entities.
     * Controlled by various loot override tweaks.
     */
    @Inject(method = "dropFromLootTable", at = @At("HEAD"), cancellable = true)
    private void NT$onDropFromLootTable(DamageSource damageSource, boolean hitByPlayer, CallbackInfo callback)
    {
        Entity entity = damageSource.getEntity();
        EntityType<?> type = this.getType();

        int luck = hitByPlayer && entity != null ? EnchantmentHelper.getMobLooting((LivingEntity) entity) : 0;
        int zeroToTwo = this.random.nextInt(3) + luck;
        int zeroToOne = this.random.nextInt(2) + luck;

        boolean isZombiePigmen = ModConfig.Gameplay.oldZombiePigmenDrops() && type == EntityType.ZOMBIFIED_PIGLIN;
        boolean isSkeleton = ModConfig.Gameplay.oldSkeletonDrops() && type == EntityType.SKELETON;
        boolean isChicken = ModConfig.Gameplay.oldChickenDrops() && type == EntityType.CHICKEN;
        boolean isRabbit = ModConfig.Gameplay.oldRabbitDrops() && type == EntityType.RABBIT;
        boolean isSheep = ModConfig.Gameplay.oldSheepDrops() && type == EntityType.SHEEP;
        boolean isStray = ModConfig.Gameplay.oldStrayDrops() && type == EntityType.STRAY;
        boolean isPig = ModConfig.Gameplay.oldPigDrops() && type == EntityType.PIG;

        boolean isCow = (ModConfig.Gameplay.oldCowDrops() && type == EntityType.COW) ||
            (ModConfig.Gameplay.oldMooshroomDrops() && type == EntityType.MOOSHROOM)
        ;

        boolean isSpider = (ModConfig.Gameplay.oldSpiderDrops() && type == EntityType.SPIDER) ||
            (ModConfig.Gameplay.oldCaveSpiderDrops() && type == EntityType.CAVE_SPIDER)
        ;

        boolean isZombie = (ModConfig.Gameplay.oldZombieDrops() && type == EntityType.ZOMBIE) ||
            (ModConfig.Gameplay.oldZombieVillagerDrops() && type == EntityType.ZOMBIE_VILLAGER) ||
            (ModConfig.Gameplay.oldDrownedDrops() && type == EntityType.DROWNED) ||
            (ModConfig.Gameplay.oldHuskDrops() && type == EntityType.HUSK)
        ;

        if (isZombiePigmen)
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.COOKED_PORKCHOP, zeroToTwo));
        else if (isChicken)
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.FEATHER, zeroToTwo));
        else if (isRabbit)
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.RABBIT_HIDE, zeroToOne));
        else if (isCow)
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.LEATHER, zeroToTwo));
        else if (isSpider)
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.STRING, zeroToTwo));
        else if (isZombie)
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.FEATHER, zeroToTwo));
        else if (isSkeleton || isStray)
        {
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.ARROW, this.random.nextInt(3) + luck));
            ItemServerUtil.splitLoot(callback, this, new ItemStack(Items.BONE, this.random.nextInt(3) + luck));
        }
        else if (isPig)
        {
            ItemLike item = this.isOnFire() ? Items.COOKED_PORKCHOP : Items.PORKCHOP;
            ItemServerUtil.splitLoot(callback, this, new ItemStack(item, zeroToTwo));
        }
        else if (isSheep)
            callback.cancel();
    }
}
