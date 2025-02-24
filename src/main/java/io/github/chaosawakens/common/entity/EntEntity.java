package io.github.chaosawakens.common.entity;

import io.github.chaosawakens.common.entity.ai.AnimatableMeleeGoal;
import io.github.chaosawakens.common.entity.ai.AnimatableMoveToTargetGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntEntity extends AnimatableMonsterEntity implements IAnimatable {
	private final AnimationFactory factory = new AnimationFactory(this);
	private boolean isDespawnable;
	
	public EntEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		this.ignoreFrustumCheck = true;
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		super.doTickBase(event);
		if(this.dead) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ent.death_animation", true));
			return PlayState.CONTINUE;
		}
		
		if(this.getHitting()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ent.attacking_animation", true));
			return PlayState.CONTINUE;
		}
		
		if(event.isMoving()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ent.walking_animation", true));
			return PlayState.CONTINUE;
		}
		
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ent.idle_animation", true));
		return PlayState.CONTINUE;
		
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 24.0F));
		this.goalSelector.addGoal(3, new LookAtGoal(this, IronGolemEntity.class, 24.0F));
		this.goalSelector.addGoal(3, new LookAtGoal(this, SnowGolemEntity.class, 24.0F));
		this.goalSelector.addGoal(2, new AnimatableMoveToTargetGoal(this, 1.6, 8));
		this.goalSelector.addGoal(2, new AnimatableMeleeGoal(this, 48.3, (progress, length) -> 0.7 < progress/length && progress/length < 0.8 ));
		this.goalSelector.addGoal(4, new RandomWalkingGoal(this, 1.6));
		this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
		this.goalSelector.addGoal(7, new SwimGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolemEntity.class, true));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
		return MobEntity.registerAttributes()
				.createMutableAttribute(Attributes.MAX_HEALTH, 150)
				.createMutableAttribute(Attributes.ARMOR, 3)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
				.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
				.createMutableAttribute(Attributes.ATTACK_SPEED, 10)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 25)
				.createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 3.5D)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 24);
	}
	
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "entcontroller", 0, this::predicate));
	}
	
	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return this.isDespawnable;
	}
	
	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
	
	public void seDespawable(boolean isDespawnable) { this.isDespawnable = isDespawnable; }
}
