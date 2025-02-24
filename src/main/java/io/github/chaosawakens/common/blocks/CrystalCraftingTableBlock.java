package io.github.chaosawakens.common.blocks;

import io.github.chaosawakens.common.gui.container.CraftingTableContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * This class nothing yet, thus 
 * 
 * @author invalid2
 *
 */
public class CrystalCraftingTableBlock extends Block {
	
	private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.crystal_crafting_table");
	
	public CrystalCraftingTableBlock(Properties properties) {
		super(properties);
	}
	
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			player.openContainer(state.getContainer(worldIn, pos));
			player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			return ActionResultType.CONSUME;
		}
	}
	
	public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
		return new SimpleNamedContainerProvider((id, inventory, player) -> {
			return new CraftingTableContainer(id, inventory, IWorldPosCallable.of(worldIn, pos), this);
		}, CONTAINER_NAME);
	}
}
