package net.stohun.veercore;

import net.fabricmc.api.ModInitializer;

import net.stohun.veercore.block.ModBlocks;
import net.stohun.veercore.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VeerCore implements ModInitializer {
	public static final String MOD_ID = "veercore";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
	}
}