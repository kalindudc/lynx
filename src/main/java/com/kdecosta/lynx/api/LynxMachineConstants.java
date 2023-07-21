package com.kdecosta.lynx.api;

import com.kdecosta.lynx.energy.EnergyUnit;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.io.Serial;
import java.util.HashMap;

public class LynxMachineConstants {
    public static final EnergyUnit GENERATOR_ENERGY_CAPACITY = new EnergyUnit(32000);
    public static final HashMap<Item, Integer> GENERATOR_BURN_RATE_IN_SECONDS = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 6309711727989258681L;

        {
            put(Items.COAL, 10);
            put(Items.COAL_BLOCK, ((int) (10 * 9 * 0.9)));
            put(Items.CHARCOAL, 10);
        }
    };
    public static final HashMap<Item, Long> GENERATOR_ENERGY_RATES = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = -7687071522124096713L;

        {
            put(Items.COAL, 5L);
            put(Items.COAL_BLOCK, ((long) (5 * 9 * 1.1)));
            put(Items.CHARCOAL, 5L);
        }
    };

    public static final EnergyUnit ENERGY_CELL_MAX_CAPACITY = new EnergyUnit(256000);
}
