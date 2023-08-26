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

    @Test
    @DisplayName("The Quality of an item is never more than 50")
    void test_qualityOfAnItemIsNeverMoreThan50() {
        // Given
        int numDays = 200;
        int sellIn = 20;
        int initialQuality = 48;
        Item randomItem = new Item("Random Item", sellIn, initialQuality);
        Item agedBrie = new Item("Aged Brie", sellIn, initialQuality);
        Item dexterityVest = new Item("+5 Dexterity Vest", sellIn, initialQuality);
        Item elixirMongoose = new Item("Elixir of the Mongoose", sellIn, initialQuality);
        Item backstagePasses = new Item("Backstage passes to a TAFKAL80ETC concert", sellIn, initialQuality);
        Item[] items = new Item[] { randomItem, agedBrie, dexterityVest, elixirMongoose, backstagePasses };

        GildedRose app = new GildedRose(items);

        // When
        for (int day = 0; day < numDays; day++) {
            app.updateQuality();
        }

        // Then
        Arrays.asList(items).forEach(item -> assertFalse(item.quality > 50));
    }

    @Test
    @DisplayName("Quality of Backstage passes increase by 1 10 days or more before sellIn")
    void test_backstagePassesIncreaseInQualityBy1_10dayOrMoreBeforeSellIn() {
        /*
         * Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but
	Quality drops to 0 after the concert
         */

        // Given
        int sellInInitialDate = 20;
        int initialQuality = 10;
        Item backstagePasses = new Item("Backstage passes to a TAFKAL80ETC concert", sellInInitialDate, initialQuality);
        Item[] items = new Item[] { backstagePasses };

        GildedRose app = new GildedRose(items);

        // Quality increases by 1 when there are 10 days or more until SellIn
        for (int day = 0; day < sellInInitialDate - 10; day++) {
            app.updateQuality();
        }

        int finalQuality10DaysBeforeSellIn = backstagePasses.quality;
        int qualityDifference10DaysBeforeSellIn = finalQuality10DaysBeforeSellIn - initialQuality;
        int qualityRate10DaysBeforeSellIn = Math.floorDiv(qualityDifference10DaysBeforeSellIn, 10);

        // Then
        assertEquals(1, qualityRate10DaysBeforeSellIn);
    }

    @Test
    @DisplayName("Quality of Backstage passes increase by 2 10 days or less before sellIn")
    void test_backstagePassesIncreaseInQualityBy2_10dayOrLessBeforeSellIn() {
        /*
         * Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but
	Quality drops to 0 after the concert
         */

        // Given
        int sellInInitialDate = 10;
        int initialQuality = 10;
        Item backstagePasses = new Item("Backstage passes to a TAFKAL80ETC concert", sellInInitialDate, initialQuality);
        Item[] items = new Item[] { backstagePasses };

        GildedRose app = new GildedRose(items);

        // Quality increases by 1 when there are 10 days or more until SellIn
        for (int day = 0; day < sellInInitialDate - 5; day++) {
            app.updateQuality();
        }

        int finalQuality5DaysBeforeSellIn = backstagePasses.quality;
        int qualityDifference5DaysBeforeSellIn = finalQuality5DaysBeforeSellIn - initialQuality;
        int qualityRate10DaysLessBeforeSellIn = Math.floorDiv(qualityDifference5DaysBeforeSellIn, 5);

        // Then
        assertEquals(2, qualityRate10DaysLessBeforeSellIn);
    }

    @Test
    @DisplayName("Quality of Backstage passes increase by 3 5 days or less before sellIn")
    void test_backstagePassesIncreaseInQualityBy3_5dayOrLessBeforeSellIn() {
        /*
         * Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but
	Quality drops to 0 after the concert
         */

        // Given
        int sellInInitialDate = 5;
        int initialQuality = 10;
        Item backstagePasses = new Item("Backstage passes to a TAFKAL80ETC concert", sellInInitialDate, initialQuality);
        Item[] items = new Item[] { backstagePasses };

        GildedRose app = new GildedRose(items);

        // Quality increases by 1 when there are 10 days or more until SellIn
        for (int day = 0; day < sellInInitialDate; day++) {
            app.updateQuality();
        }

        int finalQualityAtSellIn = backstagePasses.quality;
        int qualityDifferenceAtSellIn = finalQualityAtSellIn - initialQuality;
        int qualityRate5DayLessBeforeSellIn = Math.floorDiv(qualityDifferenceAtSellIn, 5);

        // Then
        assertEquals(3, qualityRate5DayLessBeforeSellIn);
    }

    @Test
    @DisplayName("Quality of Backstage passes drop to zero after concert")
    void test_backstagePassesQuality_dropsToZeroAfterConcert() {
        /*
         * Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but
	Quality drops to 0 after the concert
         */

        // Given
        int sellInInitialDate = 10;
        int initialQuality = 10;
        Item backstagePasses = new Item("Backstage passes to a TAFKAL80ETC concert", sellInInitialDate, initialQuality);
        Item[] items = new Item[] { backstagePasses };

        GildedRose app = new GildedRose(items);

        // Quality increases by 1 when there are 10 days or more until SellIn
        for (int day = 0; day < sellInInitialDate + 1; day++) {
            app.updateQuality();
        }

        // Then
        assertEquals(0, backstagePasses.quality);
    }

    @Test
    @DisplayName("'Conjured' items degrade in Quality twice as fast as normal items")
    void test_conjuredItemsDegradeQualityTwiceAsFastAsNormalItems() {
        // Given
        int sellInInitialDate = 4;
        int daysAfterSellIn = 4;
        int initialQuality = 40;
        Item conjured = new Item("Conjured", sellInInitialDate, initialQuality);
        Item normal = new Item("Random item", sellInInitialDate, initialQuality);
        Item[] items = new Item[] { conjured, normal };

        GildedRose app = new GildedRose(items);

        // Before sellIn
        for (int day = 0; day < sellInInitialDate; day++) {
            app.updateQuality();
        }

        // Verify quality depreciation rates
        int conjuredItemDepreciationRateBeforeSellIn = Math.floorDiv(initialQuality - conjured.quality, sellInInitialDate);
        int normalItemDepreciationRateBeforeSellIn = Math.floorDiv(initialQuality - normal.quality, sellInInitialDate);

        System.out.println("Normal quality: " + String.valueOf(normal.quality));

        assertEquals(conjuredItemDepreciationRateBeforeSellIn, normalItemDepreciationRateBeforeSellIn * 2);

        // Take snapshots of the items qualities
        int qualityAtSellInConjured = conjured.quality;
        int qualityAtSellInNormal = normal.quality;

        // After sellIn
        for (int day = 0; day < daysAfterSellIn; day++) {
            app.updateQuality();
        }

        // Verify quality depreciation rates
        int conjuredItemDepreciationRateAfterSellIn = Math.floorDiv(qualityAtSellInConjured - conjured.quality, daysAfterSellIn);
        int normalItemDepreciationRateAfterSellIn = Math.floorDiv(qualityAtSellInNormal - normal.quality, daysAfterSellIn);

        assertEquals(conjuredItemDepreciationRateAfterSellIn, normalItemDepreciationRateAfterSellIn * 2);
    }

}
