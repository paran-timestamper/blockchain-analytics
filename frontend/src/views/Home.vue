<template>
  <div class="home-layout">
    <!-- 왼쪽: 뉴스 영역 -->
    <div class="home-page">
      <h2>📡 News Search</h2>
      <!-- 검색 바 -->
      <div class="search-bar">
        <input v-model="query" @keyup.enter="searchArticles" placeholder="Enter a keyword" />
        <button @click="searchArticles">Search</button>
      </div>

      <!-- 뉴스 그리드 -->
      <div class="news-grid" v-if="articles.length">
        <div v-for="article in articles" :key="article.url" class="news-card">
          <img :src="article.image" alt="thumbnail" class="news-image" v-if="article.image"/>
          <h3>{{ article.title }}</h3>
          <p>{{ article.description }}</p>
          <a :href="article.url" target="_blank">Read more</a>
        </div>
      </div>
    </div>

    <!-- 오른쪽: 사이드바 -->
    <div class="sidebar">
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
  </div>
</template>

<script>
export default {
  data() {
    return {
      query: '',
      articles: [],
      paginatedArticles: [],
      allArticles: [],
      currentPage: 1,
      pageSize: 10,
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
    this.fetchLatestArticles();
    this.fetchTokenPrices();
  },
  methods: {
    async fetchLatestArticles() {
      try {
        const response = await fetch(window.location.host+'/api/news');  // 백엔드는 100개 반환해야 함
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        this.allArticles = data.articles;
        this.updatePaginatedArticles();  // 초기 첫 페이지
      } catch (error) {
        console.error('Latest news fetch failed:', error);
      }
    },
    async searchArticles() {
      if (!this.query || !this.query.trim()) return;

      try {
        const response = await fetch(`${window.location.origin}/api/news/search`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ query: this.query }),
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const data = await response.json();
        this.articles = data.articles || [];
      } catch (error) {
        console.error('Search failed:', error);
      }
    },


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
/* 전체 레이아웃 */
.home-layout {
  display: flex;
  gap: 24px;
  color: white;
  padding: 24px;
  background: #0d1117;
}

/* 왼쪽 뉴스 페이지 */
.home-page {
  width: 75%;
}

/* 검색 바 */
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}
input {
  flex: 1;
  padding: 8px;
  border-radius: 4px;
  border: none;
}
button {
  padding: 8px 12px;
  background: #2196f3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 뉴스 카드 */
.news-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.news-card {
  background: #1f2630;
  padding: 16px;
  border-radius: 8px;
}
.news-image {
  width: 100%;
  height: auto;
  border-radius: 4px;
  margin-bottom: 8px;
}

/* 사이드바 */
.sidebar {
  position: fixed;
  top: 100px;
  right: 40px;
  width: 240px;
  background: #1a1a1a;
  border: 1px solid #333;
  border-radius: 12px;
  padding: 20px;
  font-size: 16px;
}
.sidebar h3 {
  margin-bottom: 16px;
}

/* 토큰 카드 */
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
