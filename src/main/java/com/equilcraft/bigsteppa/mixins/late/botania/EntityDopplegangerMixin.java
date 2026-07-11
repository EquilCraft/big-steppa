package com.equilcraft.bigsteppa.mixins.late.botania;

import com.equilcraft.bigsteppa.api.BigFakePlayer;
import com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer;
import com.equilcraft.bigsteppa.common.tile.beaconfarmer.TileBeaconFarmer$;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.entity.EntityDoppleganger;

import java.util.Collections;
import java.util.List;

@Mixin(value = EntityDoppleganger.class, remap = false)
public abstract class EntityDopplegangerMixin extends Entity {
	@Shadow
	List<String> playersWhoAttacked;

	public EntityDopplegangerMixin(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "isTruePlayer", at = @At("HEAD"), cancellable = true)
	private static void bigsteppa$isTruePlayer(Entity e, CallbackInfoReturnable<Boolean> cir) {
		if (e instanceof BigFakePlayer) {
			cir.setReturnValue(true);
		}
	}

	@Inject(
		method = "onLivingUpdate", at = @At(value = "INVOKE",
		target = "Lvazkii/botania/common/entity/EntityDoppleganger;setDead()V"),
		cancellable = true,
		remap = true
	)
	private void bitsteppa$setDead(CallbackInfo ci) {
		if (this.worldObj.isRemote)
			ci.cancel();
	}

	@ModifyArg(
		method = "dropFewItems(ZI)V", at = @At(value = "INVOKE", ordinal = 2,
		target = "Lvazkii/botania/common/entity/EntityDoppleganger;entityDropItem(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;"),
		index = 0,
		remap = true
	)
	private ItemStack bigsteppa$dropFewItems(ItemStack dice) {
		if (this.playersWhoAttacked.contains(TileBeaconFarmer$.MODULE$.fakePlayerName())) {
			dice.stackSize = 0;
		}

		return dice;
	}

	@Inject(method = "getPlayersAround", at = @At("HEAD"), cancellable = true)
	private void bigsteppa$getPlayersAround(CallbackInfoReturnable<List<EntityPlayer>> cir) {
		if (this.playersWhoAttacked.contains(TileBeaconFarmer$.MODULE$.fakePlayerName())) {
			ChunkCoordinates coords = TileBeaconFarmer$.MODULE$.registry().findNearestTileWithRadius(
				this.worldObj, (int) this.posX, (int) this.posY, (int) this.posZ, 16);

			if (coords == null) return;

			TileEntity te = this.worldObj.getTileEntity(coords.posX, coords.posY, coords.posZ);

			if (!(te instanceof TileBeaconFarmer)) return;

			cir.setReturnValue(Collections.singletonList(((TileBeaconFarmer) te).fakePlayer()));
		}
	}
}
