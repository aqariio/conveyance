package aqario.conveyance.client.sound;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class PropellerLoop extends MovingSoundInstance {
    private final ClientPlayerEntity player;

    public PropellerLoop(ClientPlayerEntity player) {
        super(ConveyanceSoundEvents.ENTITY_MONOPLANE_ENGINE, SoundCategory.NEUTRAL, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0F;
        this.relative = true;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void tick() {
        if (this.player.isRemoved()) {
            this.setDone();
        }
    }
}
