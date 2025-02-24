/**
 * 
 */
package io.github.chaosawakens.common.registry;

import io.github.chaosawakens.ChaosAwakens;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

/**
 * Class dedicated to tag wrapper constants, so they can be referenced somewhere else
 * @author invalid2
 */
public class CATags {
	
	//Block tags
	public static final ITag.INamedTag<Block> BASE_STONE_CRYSTAL = BlockTags.makeWrapperTag(ChaosAwakens.MODID+":base_stone_crystal");
	public static final ITag.INamedTag<Block> CORNS = BlockTags.makeWrapperTag(ChaosAwakens.MODID+":corn");
	
	public static final ITag.INamedTag<Block> MINERS_DREAM_MINEABLE = BlockTags.makeWrapperTag(ChaosAwakens.MODID+":miners_dream_mineable");
	public static final ITag.INamedTag<Block> MINERS_DREAM_UNSAFE = BlockTags.makeWrapperTag(ChaosAwakens.MODID+":miners_dream_unsafe");
	public static final ITag.INamedTag<Block> MINERS_DREAM_TORCH_SAFE = BlockTags.makeWrapperTag(ChaosAwakens.MODID+":miners_dream_torch_safe");
	public static final ITag.INamedTag<Block> AIR_BLOCKS = BlockTags.makeWrapperTag("air");
	//Item tags
}
