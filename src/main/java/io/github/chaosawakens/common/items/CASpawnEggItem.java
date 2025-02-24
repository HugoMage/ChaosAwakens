package io.github.chaosawakens.common.items;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CASpawnEggItem extends SpawnEggItem {
	private final Supplier<? extends EntityType<?>> typeGetter;
	private final boolean isEnchanted;
	
	public CASpawnEggItem(Supplier<? extends EntityType<?>> typeIn, Properties properties, boolean isEnchanted) {
		super(null, 0xFFFFFFF, 0xFFFFFFF, properties);
		this.typeGetter = typeIn;
		this.isEnchanted = isEnchanted;
	}
	
	public CASpawnEggItem(Supplier<? extends EntityType<?>> typeIn, Properties properties) {
		this(typeIn, properties, false);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		SpawnEggItem.EGGS.put(this.getType(null), this);
		super.fillItemGroup(group, items);
	}
	
	@Override
	public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_) { return typeGetter.get(); }
	
	@Override
	public boolean hasEffect(ItemStack stack) { return this.isEnchanted || super.hasEffect(stack); }
}