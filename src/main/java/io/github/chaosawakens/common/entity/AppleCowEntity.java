package io.github.chaosawakens.common.entity;

import io.github.chaosawakens.common.registry.CAEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AppleCowEntity extends AnimalEntity {
	
	public AppleCowEntity(EntityType<? extends AppleCowEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.fromItems(Items.WHEAT), false));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
		return MobEntity.registerAttributes()
				.createMutableAttribute(Attributes.MAX_HEALTH, 10)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 10);
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_COW_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_COW_HURT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_COW_DEATH;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
	}
	
	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	@Override
	public ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
		ItemStack itemstack = playerIn.getHeldItem(hand);
		if (itemstack.getItem() == Items.BUCKET && !this.isChild()) {
			playerIn.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
			ItemStack itemstack1 = DrinkHelper.fill(itemstack, playerIn, Items.MILK_BUCKET.getDefaultInstance());
			playerIn.setHeldItem(hand, itemstack1);
			return ActionResultType.func_233537_a_(this.world.isRemote);
		} else {
			return super.getEntityInteractionResult(playerIn, hand);
		}
	}
	
	@Override
	public AppleCowEntity createChild(ServerWorld world, AgeableEntity mate) {
		return CAEntityTypes.APPLE_COW.get().create(world);
	}
	
	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return this.isChild() ? sizeIn.height * 0.95F : 1.3F;
	}
}
