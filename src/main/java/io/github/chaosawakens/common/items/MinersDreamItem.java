package io.github.chaosawakens.common.items;

import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.common.registry.CABlocks;
import io.github.chaosawakens.common.registry.CATags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class MinersDreamItem extends Item {
	private static final int HOLE_LENGTH = 36;
	private static final int HOLE_WIDTH = 4;
	private static final int HOLE_HEIGHT = 8;
	
	public MinersDreamItem(Properties builderIn) {
		super(builderIn);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		Direction direction = context.getPlacementHorizontalFacing();
		World worldIn = context.getWorld();
		
		if (worldIn.isRemote)return ActionResultType.FAIL;
		if (direction == Direction.UP || direction == Direction.DOWN)return ActionResultType.FAIL;
		
		BlockPos breakPos = context.getPos();
		int targetY = breakPos.getY() % 8;
		PlayerEntity playerIn = context.getPlayer();
		ChaosAwakens.debug("AA", targetY);
		Vector3i facing = direction.getDirectionVec();
		
		playerIn.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 1.5F);
		worldIn.addParticle(ParticleTypes.EXPLOSION.getType(), breakPos.getX(), breakPos.getY(), breakPos.getZ(), 0.25F, 0.25F, 0.25F);
		
		for (int i = 0; i < HOLE_LENGTH; i++) {
			for (int j = 0; j < HOLE_HEIGHT; j++) {
				for (int k = -HOLE_WIDTH ; k <= HOLE_WIDTH; k++) {
					int lengthDelta = i * facing.getX() + k * facing.getZ();
					int widthDelta = i * facing.getZ() + k * facing.getX();
					BlockPos targetPos = breakPos.add(lengthDelta, -targetY + j, widthDelta);
					BlockState targetState = worldIn.getBlockState(targetPos);
					if(targetState.isIn(CATags.MINERS_DREAM_MINEABLE)) {
						this.placeWoodPillars(worldIn, targetPos, i, j, k);
					}	
				}
			}
		}
		context.getPlayer().addStat(Stats.ITEM_USED.get(this));
		context.getItem().shrink(1);
		
		return ActionResultType.SUCCESS;
	}
	
	/**
	 * Places the wood pillars that the mineshaft has
	 * @param worldIn The world this is being placed on
	 * @param pos The current BlockPos being checked
	 * @param i length increment from for loop
	 * @param j height increment from for loop
	 * @param k width increment from for loop
	 */
	private void placeWoodPillars(World worldIn, BlockPos pos, int i, int j, int k) {
		if(i != 0 && i % 8 == 0) {
			if(k == -HOLE_WIDTH || k == HOLE_WIDTH) {
				if(j == HOLE_HEIGHT-1) {
					worldIn.setBlockState(pos, CABlocks.MOULDY_PLANKS.get().getDefaultState());
					return;
				}
				worldIn.setBlockState(pos, CABlocks.MOULDY_FENCE.get().getDefaultState());
				return;
			}
			
			if (j == HOLE_HEIGHT-1) {
				if(k == 0) {
					worldIn.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
					return;
				}
				worldIn.setBlockState(pos, CABlocks.MOULDY_SLAB.get().getDefaultState().with(BlockStateProperties.SLAB_TYPE, SlabType.TOP));
				return;
			}
		}
		worldIn.removeBlock(pos, false);
	}
}