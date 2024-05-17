package aqario.conveyance.common.entity.part;

import aqario.conveyance.common.entity.vehicle.MonoplaneEntity;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;

public interface MonoplaneEntityPart extends EntityPart<MonoplaneEntity> {
    @Override
    default MonoplaneEntity getOwner() {
        throw new UnsupportedOperationException("No implementation of getOwner could be found.");
    }
}
