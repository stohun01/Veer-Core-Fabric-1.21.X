package net.stohun.veercore;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.stohun.veercore.datagen.VeerCoreModelProvider;
import net.stohun.veercore.datagen.VeerCoreLootTableProvider;

public class VeerCoreDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(VeerCoreModelProvider::new);
		pack.addProvider(VeerCoreLootTableProvider::new);
	}
}