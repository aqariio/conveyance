package aqario.conveyance.common.entity.part;

import org.quiltmc.qsl.entity.multipart.api.EntityPart;
import org.quiltmc.qsl.entity.multipart.api.MultipartEntity;

public interface MonoplaneMultipartEntity extends MultipartEntity {
	@Override
	default EntityPart<?>[] getEntityParts() {
		throw new UnsupportedOperationException("No implementation of getEntityParts could be found.");
	}
}
