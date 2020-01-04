package me.petterim1.envoys;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

class ItemSlot {

    public final Item item;
    public final double chance;
    public final boolean su;

    ItemSlot(double ch, boolean su, int id, int me, int co, String na, int en, int le) {
        Item i = Item.get(id, me, co);

        if (!na.isEmpty()) {
            i.setCustomName(na);
        }

        if (en > -1 && le > 0) {
            i.addEnchantment(Enchantment.get(en).setLevel(le));
        }

        this.item = i;
        this.chance = ch;
        this.su = su;
    }
}
