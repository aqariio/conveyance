package aqario.conveyance.common.entity.damage;

import aqario.conveyance.common.Conveyance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ConveyanceDamageTypes {
    RegistryKey<DamageType> PROPELLER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Conveyance.ID, "propeller"));

    static DamageSource of(World world, RegistryKey<DamageType> key, @Nullable Entity source, @Nullable Entity attacker) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getHolderOrThrow(key), source, attacker);
    }
}
