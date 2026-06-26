package com.equilcraft.bigsteppa.mixins;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class EarlyMixinsLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public String getMixinConfig() {
		return "mixins.bigsteppa.early.json";
	}

	@Override
	public List<String> getMixins(Set<String> loadedCoreMods) {
		List<String> mixins = new ArrayList<>();

		// The parameter loadedCoreMods contains the name of coremods that are currently loaded
		// you can check this Set to decide to load certain mixins or not.
		//if (!loadedCoreMods.contains("optifine.OptiFineForgeTweaker")) {
		//    // this mixins won't be loaded if Optifine is present
		//    mixins.add("MixinClass");
		//}

		if (FMLLaunchHandler.side().isClient()) {
			// register here your mixins that should only be loaded on the client
//			mixins.add("MixinMinecraft_Example");// this is an example you should delete it and the associated mixin class as well
		} else {
			// register here your mixins that should only be loaded on the dedicated server
			// mixins.add("MixinClass");
		}

		return mixins;
	}
}
