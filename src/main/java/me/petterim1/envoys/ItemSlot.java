package me.petterim1.envoys;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

public class ItemSlot {

    public final Item item;

    public ItemSlot(int id, int meta, int count, String name, int enchantment, int level) {
        Item item = Item.get(id, meta, count);

        if (!name.isEmpty()) {
            item.setCustomName(name);
        }

        if (enchantment > -1 && level > 0) {
            item.addEnchantment(Enchantment.get(enchantment).setLevel(level));
        }

        this.item = item;
    }
}