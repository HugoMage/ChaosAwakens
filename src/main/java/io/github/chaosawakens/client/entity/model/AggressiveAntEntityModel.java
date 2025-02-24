package io.github.chaosawakens.client.entity.model;

import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.common.entity.AggressiveAntEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class AggressiveAntEntityModel extends AnimatedGeoModel<AggressiveAntEntity> {
	
	private final String textureName;
	
	public AggressiveAntEntityModel(String textureName) {
		this.textureName = textureName;
	}
	
	@Override
	public ResourceLocation getModelLocation(AggressiveAntEntity object) {
		return new ResourceLocation(ChaosAwakens.MODID, "geo/ant.geo.json");
	}
	
	@Override
	public ResourceLocation getTextureLocation(AggressiveAntEntity object) {
		return new ResourceLocation(ChaosAwakens.MODID, "textures/entity/ant/" + textureName + ".png");
	}
	
	@Override
	public ResourceLocation getAnimationFileLocation(AggressiveAntEntity object) {
		return new ResourceLocation(ChaosAwakens.MODID, "animations/ant.animation.json");
	}
	
	@Override
	public void setLivingAnimations(AggressiveAntEntity entity, Integer uniqueID, @SuppressWarnings("rawtypes") AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		
		IBone head = this.getAnimationProcessor().getBone("head");
		// ChaosAwakens.LOGGER.debug(entity);
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX((extraData.headPitch) * ((float) Math.PI / 180F));
		head.setRotationY((extraData.netHeadYaw) * ((float) Math.PI / 270F));
	}
}
