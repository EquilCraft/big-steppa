package com.equilcraft.bigsteppa.mixins.late.thaumicenergistics.stand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumicenergistics.common.tiles.TileArcaneAssembler;

@Mixin(value = TileArcaneAssembler.class, remap = false)
public class TileArcaneAssemblerMixin {
	@Inject(method = "calculateVisDiscounts", at = @At("HEAD"), cancellable = true)
	private void bigsteppa$calculateVisDiscounts(CallbackInfo ci) {
		ci.cancel();
	}
}
