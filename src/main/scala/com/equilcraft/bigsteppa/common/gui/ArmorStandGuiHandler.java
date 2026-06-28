package com.equilcraft.bigsteppa.common.gui;

import com.equilcraft.bigsteppa.client.gui.GuiArmorStand;
import com.equilcraft.bigsteppa.common.inventory.ContainerArmorStand;
import com.equilcraft.bigsteppa.common.tile.TileArmorStand;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ArmorStandGuiHandler implements IGuiHandler {

    public static final int GUI_ID = 1;

    @Override
    public Object getServerGuiElement(
        int id,
        EntityPlayer player,
        World world,
        int x,
        int y,
        int z
    ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == GUI_ID && tile instanceof TileArmorStand
            ? new ContainerArmorStand(player.inventory, (TileArmorStand) tile)
            : null;
    }

    @Override
    public Object getClientGuiElement(
        int id,
        EntityPlayer player,
        World world,
        int x,
        int y,
        int z
    ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == GUI_ID && tile instanceof TileArmorStand
            ? new GuiArmorStand(player.inventory, (TileArmorStand) tile)
            : null;
    }
}
