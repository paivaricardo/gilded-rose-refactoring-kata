package com.gildedrose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    @Test
    void foo() {
        Item[] items = new Item[] { new Item("foo", 0, 0) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals("fixme", app.items[0].name);
    }

    @Test
    void test_agedBrieIncreasesInQualityTheOlderItGets() {
        // Given
        int numDays = 10;

        // Items
        Item agedBrie = new Item("Aged Brie", 120, 20);

        // App
        Item[] items = new Item[] {agedBrie};
        GildedRose app = new GildedRose(items);

        // When
        for (int day = 0; day < numDays; day++) {
            app.updateQuality();
        }

        // Then
        assertEquals(30, agedBrie.quality);
    }

    @Test
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



}
