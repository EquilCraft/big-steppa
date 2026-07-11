package com.equilcraft.bigsteppa.mixins.late.thaumicenergistics.stand;

import appeng.tile.grid.AENetworkInvTile;
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand;
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand$;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.IWarpingGear;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.integration.tc.VisCraftingHelper;
import thaumicenergistics.common.tiles.TileArcaneAssembler;

import java.util.HashSet;
import java.util.Hashtable;

@Mixin(value = TileArcaneAssembler.class, remap = false)
public abstract class TileArcaneAssemblerMixin extends AENetworkInvTile {
	@Unique
	private static final int bigsteppa$radiusUpdates = 16;

	@Shadow
	private float warpPowerMultiplier;

	@Shadow
	@Final
	private Hashtable<Aspect, Float> visDiscount;

	@Inject(method = "calculateVisDiscounts", at = @At("HEAD"), cancellable = true)
	private void bigsteppa$calculateVisDiscounts(CallbackInfo ci) {
		ci.cancel();
	}

	@ModifyConstant(method = "getDrops", constant = @Constant(intValue = 4))
	private int bigsteppa$getDrops(int value) {
		return 0;
	}

	@Inject(method = "onTick", at = @At("HEAD"))
	private void  bigsteppa$onTick(CallbackInfo ci) {
		if (this.worldObj.getTotalWorldTime() % 100 == 0) {
			float discount;
			this.warpPowerMultiplier = 1.0F;
			HashSet<Item> armors = new HashSet<>();
			ObjectArrayList<TileArmorStand> stands = TileArmorStand$.MODULE$.registry()
				.findAllTilesWithRadius(this.worldObj, this.xCoord, this.yCoord, this.zCoord, bigsteppa$radiusUpdates);

			for (TileArmorStand stand : stands) {
				for (int i = 0; i < 4; i++) {
					ItemStack itemStack = stand.getStackInSlot(i);

					if (itemStack != null && itemStack.getItem() != null &&
						(itemStack.getItem() instanceof IVisDiscountGear || itemStack.getItem() instanceof IWarpingGear))
					{
						armors.add(itemStack.getItem());
					}
				}
			}

			for (Aspect primal : TileArcaneAssembler.PRIMALS) {
				discount = VisCraftingHelper.INSTANCE.getScepterVisModifier(primal);

				for (Item armor : armors) {
					if (armor instanceof IVisDiscountGear) {
						discount -= ((IVisDiscountGear) armor).getVisDiscount(null, null, primal) / 100.0F;
					}
				}

				this.visDiscount.put(primal, discount < 0 ? 0 : discount);
			}

			for (Item armor : armors) {
				if (armor instanceof IWarpingGear) {
					this.warpPowerMultiplier +=
						(float) (((IWarpingGear) armor).getWarp(null, null) * TileArcaneAssembler.WARP_POWER_PERCENT);
				}
			}
		}
	}
}
