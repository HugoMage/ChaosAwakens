package io.github.chaosawakens.common.events;

import com.mojang.serialization.Codec;
import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.api.CAReflectionHelper;
import io.github.chaosawakens.api.FeatureWrapper;
import io.github.chaosawakens.common.config.CAConfig;
import io.github.chaosawakens.common.network.PacketHandler;
import io.github.chaosawakens.common.registry.CABiomes;
import io.github.chaosawakens.common.registry.CAStructures;
import io.github.chaosawakens.common.registry.CAVillagers;
import io.github.chaosawakens.common.worldgen.ConfiguredStructures;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author invalid2
 */
public class CommonSetupEvent {
	
	/**
	 * List for configured features, so they get registered at the correct time
	 */
	public static List<FeatureWrapper> configFeatures = new ArrayList<>();
	
	private static Method codecMethod;
	
	public static void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
		PacketHandler.init();
		Raid.WaveMember.create("illusioner", EntityType.ILLUSIONER, new int[] { 0,0, 0, 0, 1, 1, 0, 2 });

		event.enqueueWork(() -> {
			CAStructures.setupStructures();
			ConfiguredStructures.registerConfiguredStructures();
			CAVillagers.registerVillagerTypes();
			
			CAReflectionHelper.classLoad("io.github.chaosawakens.common.registry.CAConfiguredFeatures");
			configFeatures.forEach((wrapper) -> Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, wrapper.getIdentifier(), wrapper.getFeatureType()));
		});
		
		// TODO Make it so we don't have to add stuff here manually
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.MINING_BIOME.getId()), CABiomes.Type.MINING_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.STALAGMITE_VALLEY.getId()), CABiomes.Type.MINING_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.VILLAGE_PLAINS.getId()), CABiomes.Type.VILLAGE_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.VILLAGE_SAVANNA.getId()), CABiomes.Type.VILLAGE_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.VILLAGE_TAIGA.getId()), CABiomes.Type.VILLAGE_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.VILLAGE_SNOWY.getId()), CABiomes.Type.VILLAGE_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.VILLAGE_DESERT.getId()), CABiomes.Type.VILLAGE_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.DANGER_ISLANDS.getId()), CABiomes.Type.DANGER_DIMENSION);
		BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, CABiomes.CRYSTAL_PLAINS.getId()), CABiomes.Type.CRYSTAL_DIMENSION);
	}
	
	public static void addDimensionalSpacing(final WorldEvent.Load event) {
		if (event.getWorld() instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) event.getWorld();
			ServerChunkProvider chunkProvider = serverWorld.getChunkProvider();
			
			try {
				if (codecMethod == null)codecMethod = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "codec");
				// TODO Fix this
				ResourceLocation cgRL = Registry.CHUNK_GENERATOR_CODEC.getKey((Codec<? extends ChunkGenerator>) codecMethod.invoke(chunkProvider.generator));
				if (cgRL != null && cgRL.getNamespace().equals("terraforged"))return;
			} catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
				ChaosAwakens.warn("WORLDGEN", e);
				e.printStackTrace();
			} catch(UnableToFindMethodException e) {
				if (CAConfig.COMMON.terraforgedCheckMsg.get())
					ChaosAwakens.info("WORLDGEN", "Unable to check if " + serverWorld.getDimensionKey().getLocation()
						+ " is using Terraforged's ChunkGenerator due to Terraforged not being present or not accessable,"
						+ " if you aren't using Terraforged please ignore this message");
			}
			
			if (serverWorld.getChunkProvider().getChunkGenerator() instanceof FlatChunkGenerator && serverWorld.getDimensionKey().equals(World.OVERWORLD))return;
			
			Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(chunkProvider.generator.func_235957_b_().func_236195_a_());
			tempMap.putIfAbsent(CAStructures.ENT_DUNGEON.get(), DimensionStructuresSettings.field_236191_b_.get(CAStructures.ENT_DUNGEON.get()));
			tempMap.putIfAbsent(CAStructures.WASP_DUNGEON.get(), DimensionStructuresSettings.field_236191_b_.get(CAStructures.WASP_DUNGEON.get()));
			chunkProvider.generator.func_235957_b_().field_236193_d_ = tempMap;
		}
	}
}