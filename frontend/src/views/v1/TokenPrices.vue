<template>
  <div class="token-price-sidebar">
    <h3>📊 Token Prices</h3>
    <div class="token-box" v-for="token in tokens" :key="token.name">
      <div class="token-header">
        <img :src="token.icon" class="token-icon" />
        <span class="token-name">{{ token.name }}</span>
      </div>
      <div class="token-price">
        ${{ token.price.toLocaleString() }}
      </div>
      <div
          class="token-change"
          :class="{ positive: token.change > 0, negative: token.change < 0 }"
      >
        {{ token.change > 0 ? '+' : '' }}{{ token.change.toFixed(2) }}
        ({{ token.percentChange.toFixed(2) }}%)
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TokenPriceSidebar',
  data() {
    return {
      tokens: [
        {
          name: 'Bitcoin',
          icon: 'https://cryptologos.cc/logos/bitcoin-btc-logo.png',
          price: 0,
          change: 0,
          percentChange: 0
        },
        {
          name: 'Ethereum',
          icon: 'https://cryptologos.cc/logos/ethereum-eth-logo.png',
          price: 0,
          change: 0,
          percentChange: 0
        },
        {
          name: 'Solana',
          icon: 'https://cryptologos.cc/logos/solana-sol-logo.png',
          price: 0,
          change: 0,
          percentChange: 0
        }
      ]
    };
  },
  mounted() {
    this.fetchTokenPrices();
  },
  methods: {
    async fetchTokenPrices() {
      try {
        const res = await fetch(
            "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=bitcoin,ethereum,solana"
        );
        const data = await res.json();
        this.tokens = data.map(coin => ({
          name: coin.name,
          icon: coin.image,
          price: coin.current_price,
          change: coin.price_change_24h,
          percentChange: coin.price_change_percentage_24h
        }));
      } catch (error) {
        console.error('Failed to fetch token prices:', error);
      }
    }
  }
};
</script>

<style scoped>
.token-price-sidebar {
  background: #1a1a1a !important;
  border: 1px solid #333;
  border-radius: 12px;
  padding: 20px;
  font-size: 16px;
  width: 180px;
}
.token-price-sidebar h3 {
  margin-bottom: 16px;
}
.token-box {
  background: #1f2630;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 16px;
}
.token-header {
  display: flex;
  align-items: center;
  gap: 8px;
}
.token-icon {
  width: 20px;
  height: 20px;
}
.token-name {
  font-weight: bold;
}
.token-price {
  font-size: 18px;
  margin-top: 4px;
}
.token-change {
  font-size: 14px;
}
.token-change.positive {
  color: #4caf50;
}
.token-change.negative {
  color: #f44336;
}
</style>
