<template>
  <div class="home-page">
    <h2>📡 News Search</h2>
    <div class="search-bar">
      <input v-model="query" @keyup.enter="searchArticles" placeholder="Enter a keyword" />
      <button @click="searchArticles">Search</button>
    </div>

    <div class="news-grid" v-if="articles.length">
      <div v-for="article in articles" :key="article.url" class="news-card">
        <img :src="article.image" alt="thumbnail" class="news-image" v-if="article.image"/>
        <h3>{{ article.title }}</h3>
        <p>{{ article.description }}</p>
        <a :href="article.url" target="_blank">Read more</a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      query: '',
      articles: []
    };
  },
  mounted() {
    this.fetchLatestArticles(); // 페이지 로딩 시 최신 기사 10개 불러옴
  },
  methods: {
    async fetchLatestArticles() {
      const API_KEY = '84fd2c132efdd2f93c83a3ba0735ad7d'; // 너의 GNews API 키로 바꿔줘!
      const url = `https://gnews.io/api/v4/top-headlines?lang=en&max=10&sortby=publishedAt&token=${API_KEY}`;

      try {
        const response = await fetch(url);
        const data = await response.json();
        this.articles = data.articles;
      } catch (error) {
        console.error('Latest news fetch failed:', error);
      }
    },
    async searchArticles() {
      if (!this.query.trim()) return;

      const API_KEY = 'YOUR_API_KEY_HERE'; // 동일한 API 키
      const url = `https://gnews.io/api/v4/search?q=${encodeURIComponent(this.query)}&lang=en&max=10&sortby=publishedAt&token=${API_KEY}`;

      try {
        const response = await fetch(url);
        const data = await response.json();
        this.articles = data.articles;
      } catch (error) {
        console.error('Search failed:', error);
      }
    }
  }
};
</script>

<style scoped>
.home-page {
  padding: 24px;
  color: white;
}
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
.news-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.news-card {
  background: #1f2630;
  color: white;
  padding: 16px;
  border-radius: 8px;
}
.news-image {
  width: 100%;
  height: auto;
  border-radius: 4px;
  margin-bottom: 8px;
}
</style>
