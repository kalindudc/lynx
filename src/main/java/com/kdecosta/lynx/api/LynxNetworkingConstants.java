package com.kdecosta.lynx.api;

import com.kdecosta.lynx.Lynx;
import net.minecraft.util.Identifier;

public class LynxNetworkingConstants {

    public static final Identifier GENERATOR_PACKET_ID = new Identifier(Lynx.MODID, "generator_screen_update_packet");
    public static final Identifier ENERGY_CELL_PACKET_ID = new Identifier(Lynx.MODID, "energy_cell_screen_update_packet");
    public static final Identifier DEREGISTER_PLAYER_PACKET_ID = new Identifier(Lynx.MODID, "deregister_player_update_packet");
    public static final Identifier UPDATE_SIDES_PACKET_ID = new Identifier(Lynx.MODID, "energy_sides_update_packet");
}
