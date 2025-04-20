package minigee.repairsmith.util;

public class NumismaticUtils {
    public static CoinsTuple convertCostToCoins(int cost) {
        return convertCostToCoins((long) cost);
    }

    public static CoinsTuple convertCostToCoins(long cost) {
        return new CoinsTuple(
    cost / 10000,
    (cost % 10000) / 100,
    cost % 100
        );
    }

    public static class CoinsTuple {
        public long goldCoins;
        public long silverCoins;
        public long bronzeCoins;

        public CoinsTuple(long goldCoins, long silverCoins, long bronzeCoins) {
            this.bronzeCoins = bronzeCoins;
            this.silverCoins = silverCoins;
            this.goldCoins = goldCoins;
        }
    }
}
