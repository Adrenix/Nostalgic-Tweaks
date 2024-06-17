package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicManager.class)
public interface MusicManagerAccess
{
    @Accessor("nextSongDelay")
    void nt$setNextSongDelay(int delay);
}
