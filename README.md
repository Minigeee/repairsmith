# Repairsmith

This mod adds the Repairsmith villager profession, providing an alternative way to repair items. The Repairsmith can repair any damaged item for an emerald cost, allowing for an additional item repair method in addition to the Mending enchantment and anvil repairs. The goal is to make repairing the items you've spent several hours crafting and enchanting a little less tedious to maintain.

## Description

- Repairsmiths charge emeralds to fix items, with costs based on how much durability is missing
- Their workstation is the anvil, and they support most other villager features including, trade discounts and xp rewards for both the player and the villager during repair transactions
- You can level up the Repairsmith, though there aren't currently extra perks for doing so (open to ideas!)
- Villages may generate with a repair workshop, where you'll find the Repairsmith
- Compat with CTOV and Numismatic Overhaul is not supported yet (but planned)

## Configuration

Settings for this mod can be configured in the **repairsmith.json** config file:

```json5
// Cost equation: cost = (durability / durabilityPerEmerald) ^ costExp

{
  // The amount of durability repair 1 emerald can afford (increasing this decreases repair cost)
  "durabilityPerEmerald": 30,
  // The exponent value that gets applied to the emerald cost, before villager discounts are applied (increasing this increases repair cost)
  "costExp": 0.7,
  // The number of repair trades offered before the repairsmith must restock
  "maxOffers": 5,
  // Amount of durability needed to be repaired to reward player with 1 xp (increasing this decreases rewarded xp)
  "durabilityPerPlayerXp": 50,
  // The exponent value that gets applied to the rewarded xp (increasing this increases rewarded xp)
  "xpExp": 0.6
}
```