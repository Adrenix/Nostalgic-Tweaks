package mod.adrenix.nostalgic.mixin.duck;

/**
 * Adds an attack counter to Ghast mobs since the charging code was removed in 1.8.
 */
public interface GhastCounter
{
    int nt$getAttackCounter();
}
