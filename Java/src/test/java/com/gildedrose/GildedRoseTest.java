package com.gildedrose;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GildedRoseTest {

    @Test
    @DisplayName("'Aged Brie' actually increases in Quality the older it gets")
    void test_agedBrieIncreasesInQualityTheOlderItGets() {
        // Given
        int numDays = 10;

        int initialQuality = 20;
        int finalQuality;

        // Items
        Item agedBrie = new Item("Aged Brie", 120, initialQuality);

        // App
        Item[] items = new Item[] { agedBrie };
        GildedRose app = new GildedRose(items);

        // When
        for (int day = 0; day < numDays; day++) {
            app.updateQuality();
        }

        finalQuality = agedBrie.quality;

        // Then
        assertTrue(finalQuality > initialQuality);
    }

    @Test
    @DisplayName("'Sulfuras', being a legendary item, never has to be sold or decreases in Quality")
    void test_sulfurasNeverDecreaseQuality() {
        // Given
        int numDays = 10;
        Item sulfuras = new Item("Sulfuras, Hand of Ragnaros", 0, 80);
        Item[] items = new Item[] { sulfuras };
        GildedRose app = new GildedRose(items);

        // When
        for (int day = 0; day < numDays; day++) {
            app.updateQuality();
        }

        // Then
        assertEquals(80, sulfuras.quality);

    }

    @Test
    @DisplayName("Once the sell by date has passed, Quality degrades twice as fast")
    void test_when_sellByDatePasses_thenQualityShouldDegradeTwiceAsFast() {
        // Given
        int initialQuality = 40;
        int initialSellIn = 10;
        int dayAfterSellIn = 10;

        Item randomItem = new Item("Random Item", initialSellIn, initialQuality);
        Item[] items = new Item[] { randomItem };
        GildedRose app = new GildedRose(items);

        // When
        for (int day = 0; day < initialSellIn; day++) {
            app.updateQuality();
        }

        // Get snapshot of the quality situation and degrade rate when sell-in expires
        int finalQualityWhenSellInExpires = randomItem.quality;
        int qualityDifferenceWhenSellInExpires = initialQuality - finalQualityWhenSellInExpires;
        int qualityDegradeRateUntilSellIn = Math.floorDiv(qualityDifferenceWhenSellInExpires, initialSellIn);

        for (int day = 0; day < dayAfterSellIn; day++) {
            app.updateQuality();
        }

        // Get snapshot of the quality situation and degrade rate after sell-in has
        // expired
        int finalQualityAfterSellInHasExpired = randomItem.quality;
        int qualityDifferenceAfterSellInHasExpired = finalQualityWhenSellInExpires - finalQualityAfterSellInHasExpired;
        int qualityDegradeRateAfterSellInHasExpired = Math.floorDiv(qualityDifferenceAfterSellInHasExpired,
                dayAfterSellIn);

        // Then
        assertEquals(qualityDegradeRateAfterSellInHasExpired, 2 * qualityDegradeRateUntilSellIn);
    }

    @Test
    @DisplayName("The Quality of an item is never negative")
    void test_qualityOfItemIsNeverNegative() {
        // Given
        int numDays = 200;
        int sellIn = 2;
        int initialQuality = 5;
        Item randomItem = new Item("Random Item", sellIn, initialQuality);
        Item sulfuras = new Item("Sulfuras, Hand of Ragnaros", sellIn, 80);
        Item agedBrie = new Item("Aged Brie", sellIn, initialQuality);
        Item dexterityVest = new Item("+5 Dexterity Vest", sellIn, initialQuality);
        Item elixirMongoose = new Item("Elixir of the Mongoose", sellIn, initialQuality);
        Item backstagePasses = new Item("Backstage passes to a TAFKAL80ETC concert", sellIn, initialQuality);
        Item[] items = new Item[] { randomItem, sulfuras, agedBrie, dexterityVest, elixirMongoose, backstagePasses };

        GildedRose app = new GildedRose(items);

        // When
        for (int day = 0; day < numDays; day++) {
            app.updateQuality();
        }

        List<Integer> finalItemQualities = Arrays.asList(items).stream().map(item -> item.quality)
                .collect(Collectors.toList());

        // Then
        finalItemQualities.forEach(quality -> assertFalse(quality < 0));
    }

    

}
