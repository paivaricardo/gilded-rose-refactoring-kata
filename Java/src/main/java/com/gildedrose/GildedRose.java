package com.gildedrose;

class GildedRose {
    Item[] items;

    final static String SULFURAS_HAND_HAGNAROS = "Sulfuras, Hand of Ragnaros";
    final static String AGED_BRIE = "Aged Brie";
    final static String BACKSTAGE_PASSES = "Backstage passes to a TAFKAL80ETC concert";
    final static String CONJURED = "Conjured";

    public GildedRose(Item[] items) {
        this.items = items;
    }

    private void treatSulfuras(Item item) {
        item.quality = 80;
    }

    private int getBackstagePassesAppreciationRate(Item item) {
        if (item.sellIn <= 5) {
            return 3;
        } else if (item.sellIn <= 10) {
            return 2;
        } else {
            return 1;
        }
    }

    private void appreciateAgedBrie(Item item) {
        alterItemQuality(item, 1);
    }

    private void appreciateBackstagePasses(Item item) {
        if (item.sellIn <= 0) {
            item.quality = 0;
        } else {
            alterItemQuality(item, getBackstagePassesAppreciationRate(item));
        }
    }

    private void depreciateItemQuality(Item item, int factor) {
        int qualityDepreciationRate = item.sellIn <= 0 ? (2 * factor) : factor;
        alterItemQuality(item, -qualityDepreciationRate);
    }

    private void decreaseSellInDate(Item item) {
        item.sellIn -= 1;
    }

    private void alterItemQuality(Item item, int rate) {
        item.quality += rate;

        if (item.quality > 50) {
            item.quality = 50;
        } else if (item.quality < 0) {
            item.quality = 0;
        }
    }

    public void updateQuality() {
        for (Item item : items) {

            if (item.name.equals(GildedRose.SULFURAS_HAND_HAGNAROS)) {
                treatSulfuras(item);
                continue;
            }

            if (item.quality > 0 && item.quality < 50) {
                switch (item.name) {
                    case GildedRose.AGED_BRIE:
                        appreciateAgedBrie(item);
                        break;
                    case GildedRose.BACKSTAGE_PASSES:
                        appreciateBackstagePasses(item);
                        break;
                    case GildedRose.CONJURED:
                        depreciateItemQuality(item, 2);
                        break;
                    default:
                        depreciateItemQuality(item, 1);
                        break;
            }
                }

            decreaseSellInDate(item);
        }
    }

}
