package com.equilcraft.bigsteppa.common.block;

import com.equilcraft.bigsteppa.BigSteppa$;
import com.equilcraft.bigsteppa.common.gui.ArmorStandGuiHandler;
import com.equilcraft.bigsteppa.common.tile.TileArmorStand;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockArmorStand extends BlockContainer {

    private boolean removingOtherHalf;

    public BlockArmorStand() {
        super(Material.wood);
        setHardness(2.5F);
        setResistance(5.0F);
        setStepSound(soundTypeWood);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon("minecraft:planks_oak");
    }

    @Override
    public IIcon getIcon(int side, int metadata) {
        return blockIcon;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public int getMobilityFlag() {
        return 2;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int rotation = world.getBlockMetadata(x, y, z) & 3;
        if ((rotation & 1) == 0) {
            setBlockBounds(0.27F, 0.0F, 0.05F, 0.73F, 1.0F, 0.95F);
        } else {
            setBlockBounds(0.05F, 0.0F, 0.27F, 0.95F, 1.0F, 0.73F);
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addCollisionBoxesToList(
        World world,
        int x,
        int y,
        int z,
        AxisAlignedBB mask,
        List boxes,
        Entity entity
    ) {
        setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, mask, boxes, entity);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return y < world.getHeight() - 1
            && super.canPlaceBlockAt(world, x, y, z)
            && world.isAirBlock(x, y + 1, z);
    }

    @Override
    public void onBlockPlacedBy(
        World world,
        int x,
        int y,
        int z,
        EntityLivingBase placer,
        ItemStack stack
    ) {
        int rotation = (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) + 1) & 3;
        world.setBlockMetadataWithNotify(x, y, z, rotation, 3);
        world.setBlock(x, y + 1, z, this, rotation | 4, 3);
    }

    @Override
    public boolean onBlockActivated(
        World world,
        int x,
        int y,
        int z,
        EntityPlayer player,
        int side,
        float hitX,
        float hitY,
        float hitZ
    ) {
        int metadata = world.getBlockMetadata(x, y, z);
        int lowerY = (metadata & 4) == 0 ? y : y - 1;
        TileEntity tile = world.getTileEntity(x, lowerY, z);
        if (!(tile instanceof TileArmorStand)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        TileArmorStand armorStand = (TileArmorStand) tile;
        int standSlot = clickedSlot(metadata, hitY);
        if (player.isSneaking()) {
            boolean powered = world.isBlockIndirectlyGettingPowered(x, lowerY, z)
                || world.isBlockIndirectlyGettingPowered(x, lowerY + 1, z);
            if (powered) {
                for (int slot = 0; slot < TileArmorStand.SLOT_COUNT; slot++) {
                    swapWithPlayer(player, armorStand, slot);
                }
            } else {
                swapWithPlayer(player, armorStand, standSlot);
            }
            syncPlayerInventory(player);
            return true;
        }

        ItemStack held = player.getHeldItem();
        if (held != null
            && armorStand.getStackInSlot(standSlot) == null
            && armorStand.isItemValidForSlot(standSlot, held)) {
            ItemStack inserted = held.copy();
            inserted.stackSize = 1;
            armorStand.setInventorySlotContents(standSlot, inserted);
            held.stackSize--;
            if (held.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            syncPlayerInventory(player);
            return true;
        }

        player.openGui(
            BigSteppa$.MODULE$,
            ArmorStandGuiHandler.GUI_ID,
            world,
            x,
            lowerY,
            z
        );
        return true;
    }

    private static int clickedSlot(int metadata, float hitY) {
        if ((metadata & 4) == 0) {
            return hitY < 0.5F ? TileArmorStand.SLOT_FEET : TileArmorStand.SLOT_LEGS;
        }
        return hitY < 0.5F ? TileArmorStand.SLOT_CHEST : TileArmorStand.SLOT_HEAD;
    }

    private static void swapWithPlayer(
        EntityPlayer player,
        TileArmorStand armorStand,
        int standSlot
    ) {
        int playerArmorSlot = 3 - standSlot;
        ItemStack onStand = armorStand.getStackInSlot(standSlot);
        ItemStack onPlayer = player.inventory.armorInventory[playerArmorSlot];
        armorStand.setInventorySlotContents(standSlot, onPlayer);
        player.inventory.armorInventory[playerArmorSlot] = onStand;
        player.inventory.markDirty();
    }

    private static void syncPlayerInventory(EntityPlayer player) {
        player.inventory.markDirty();
        if (player instanceof EntityPlayerMP) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int metadata) {
        if (removingOtherHalf) {
            super.breakBlock(world, x, y, z, oldBlock, metadata);
            return;
        }

        int lowerY = (metadata & 4) == 0 ? y : y - 1;
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, lowerY, z);
            if (tile instanceof TileArmorStand) {
                dropContents(world, x, lowerY, z, (TileArmorStand) tile);
            }
        }

        int otherY = (metadata & 4) == 0 ? y + 1 : y - 1;
        removingOtherHalf = true;
        try {
            if (world.getBlock(x, otherY, z) == this) {
                world.setBlockToAir(x, otherY, z);
            }
        } finally {
            removingOtherHalf = false;
        }
        super.breakBlock(world, x, y, z, oldBlock, metadata);
    }

    private static void dropContents(
        World world,
        int x,
        int y,
        int z,
        TileArmorStand armorStand
    ) {
        Random random = world.rand;
        for (int slot = 0; slot < armorStand.getSizeInventory(); slot++) {
            ItemStack stack = armorStand.getStackInSlot(slot);
            if (stack == null) {
                continue;
            }
            ItemStack dropped = stack.copy();
            EntityItem entity = new EntityItem(
                world,
                x + 0.15D + random.nextFloat() * 0.7D,
                y + 0.2D + random.nextFloat() * 0.8D,
                z + 0.15D + random.nextFloat() * 0.7D,
                dropped
            );
            if (dropped.hasTagCompound()) {
                dropped.setTagCompound((NBTTagCompound) dropped.getTagCompound().copy());
            }
            entity.motionX = random.nextGaussian() * 0.05D;
            entity.motionY = random.nextGaussian() * 0.05D + 0.2D;
            entity.motionZ = random.nextGaussian() * 0.05D;
            world.spawnEntityInWorld(entity);
        }
        armorStand.clearWithoutSync();
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int damageDropped(int metadata) {
        return 0;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return (metadata & 4) == 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return (metadata & 4) == 0 ? new TileArmorStand() : null;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        if (axis != ForgeDirection.UP && axis != ForgeDirection.DOWN) {
            return false;
        }
        int metadata = world.getBlockMetadata(x, y, z);
        int lowerY = (metadata & 4) == 0 ? y : y - 1;
        if (!(world.getTileEntity(x, lowerY, z) instanceof TileArmorStand)) {
            return false;
        }

        int rotation = metadata & 3;
        rotation = axis == ForgeDirection.UP ? (rotation + 1) & 3 : (rotation + 3) & 3;
        world.setBlockMetadataWithNotify(x, lowerY, z, rotation, 3);
        if (world.getBlock(x, lowerY + 1, z) == this) {
            world.setBlockMetadataWithNotify(x, lowerY + 1, z, rotation | 4, 3);
        }
        return true;
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
        return new ForgeDirection[] {ForgeDirection.UP, ForgeDirection.DOWN};
    }
}
