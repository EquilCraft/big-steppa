package com.equilcraft.bigsteppa.mixins;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class LateMixinsLoader implements ILateMixinLoader {
	@Override
	public String getMixinConfig() {
		return "mixins.bigsteppa.late.json";
	}

	@Nonnull
	@Override
	public List<String> getMixins(Set<String> loadedMods) {
//		Arrays.asList("%%LATE_MIXINS%%"); // TODO: rewrite for autodetect mixins on build
		List<String> mixins = new ArrayList<>();
		mixins.add("botania.EntityDopplegangerMixin");
		mixins.add("thaumicenergistics.stand.TileArcaneAssemblerMixin");
		mixins.add("thaumicenergistics.stand.TileArcaneAssembler$AAInvMixin");
		mixins.add("thaumicenergistics.stand.ContainerArcaneAssemblerMixin");
		return mixins;
	}
}
