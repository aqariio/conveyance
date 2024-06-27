package aqario.conveyance.client.sound;

import aqario.conveyance.common.Conveyance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ConveyanceSoundEvents {
    public static final SoundEvent ENTITY_MONOPLANE_ENGINE = register("entity.monoplane.engine");

    private static SoundEvent register(String id) {
        Identifier identifier = new Identifier(Conveyance.ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void init() {
    }
}
