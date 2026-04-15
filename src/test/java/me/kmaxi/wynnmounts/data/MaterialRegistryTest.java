package me.kmaxi.wynnmounts.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaterialRegistryTest {

    @Test
    void eachTierHasEightMaterials() {
        assertEquals(112, MaterialRegistry.ALL.size());
        for (int tier : MaterialRegistry.TIER_THRESHOLDS) {
            List<Material> mats = MaterialRegistry.materialsAtTier(tier);
            assertEquals(8, mats.size(), "Tier " + tier + " should have 8 materials");
        }
    }

    @Test
    void maxUsableTierBoundaries() {
        assertEquals(1,   MaterialRegistry.maxUsableTier(0));
        assertEquals(1,   MaterialRegistry.maxUsableTier(1));
        assertEquals(1,   MaterialRegistry.maxUsableTier(9));
        assertEquals(10,  MaterialRegistry.maxUsableTier(10));
        assertEquals(10,  MaterialRegistry.maxUsableTier(19));
        assertEquals(20,  MaterialRegistry.maxUsableTier(20));
        assertEquals(110, MaterialRegistry.maxUsableTier(114));
        assertEquals(115, MaterialRegistry.maxUsableTier(115));
        assertEquals(115, MaterialRegistry.maxUsableTier(200));
    }
}
