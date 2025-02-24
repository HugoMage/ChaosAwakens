package io.github.chaosawakens.common.entity;

import io.github.chaosawakens.common.entity.ai.AnimatableMeleeGoal;
import io.github.chaosawakens.common.entity.ai.AnimatableMoveToTargetGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class WaspEntity extends AnimatableMonsterEntity implements IAnimatable, IFlyingAnimal {
	private final AnimationFactory factory = new AnimationFactory(this);

	public WaspEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new FlyingMovementController(this, 20, true);
		this.ignoreFrustumCheck = true;
	}

	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		super.doTickBase(event);
		if(this.dead) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("death", true));
			return PlayState.CONTINUE;
		}
		
		if(this.getHitting()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("attacking", true));
			return PlayState.CONTINUE;
		}

		event.getController().setAnimation(new AnimationBuilder().addAnimation("flying", true));
		return PlayState.CONTINUE;

	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 24.0F));
		this.goalSelector.addGoal(3, new LookAtGoal(this, BeeEntity.class, 24.0F));
		this.goalSelector.addGoal(2, new AnimatableMoveToTargetGoal(this, 1.6, 8));
		this.goalSelector.addGoal(2, new AnimatableMeleeGoal(this, 48.3, (progress, length) -> 0.7 < progress/length && progress/length < 0.8 ));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.6));
		this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
		this.goalSelector.addGoal(7, new SwimGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, BeeEntity.class, true));
	}
	
//	public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
//		return MobEntity.registerAttributes()
//				.createMutableAttribute(Attributes.MAX_HEALTH, 150)
//				.createMutableAttribute(Attributes.ARMOR, 3)
//				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
//				.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
//				.createMutableAttribute(Attributes.ATTACK_SPEED, 10)
//				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 25)
//				.createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 3.5D)
//				.createMutableAttribute(Attributes.FOLLOW_RANGE, 24);
//	}

	public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
		return MobEntity.registerAttributes()
				.createMutableAttribute(Attributes.MAX_HEALTH, 80)
				.createMutableAttribute(Attributes.ARMOR, 10)
				.createMutableAttribute(Attributes.FLYING_SPEED, 0.8F)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 12)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 36);
	}

	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir();
			}

		};
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(false);
		return flyingpathnavigator;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeBeeStingDamage(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
		if (flag) {
			this.applyEnchantments(this, entityIn);
			if (entityIn instanceof LivingEntity) {
				((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, rand.nextInt(20) * 20, 0));
			}

			this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
		}

		return flag;
	}
	
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "waspcontroller", 0, this::predicate));
	}

	protected SoundEvent getAmbientSound() {
		return null;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_BEE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BEE_DEATH;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.6F;
	}


	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	protected boolean makeFlySound() {
		return true;
	}

	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}
	
	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
