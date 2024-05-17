package aqario.conveyance.common.entity.part;

import aqario.conveyance.common.entity.vehicle.GalleonEntity;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;

public interface GalleonEntityPart extends EntityPart<GalleonEntity> {
    @Override
    default GalleonEntity getOwner() {
        throw new UnsupportedOperationException("No implementation of getOwner could be found.");
    }
}
