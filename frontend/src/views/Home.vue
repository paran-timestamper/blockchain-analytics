<template>
  <div class="home-page">
    <h2>📡 뉴스 검색</h2>
    <div class="search-bar">
      <input v-model="query" @keyup.enter="searchArticles" placeholder="검색어를 입력하세요" />
      <button @click="searchArticles">검색</button>
    </div>

    <div v-if="articles.length">
      <div v-for="article in articles" :key="article.url" class="news-card">
        <h3>{{ article.title }}</h3>
        <p>{{ article.description }}</p>
        <a :href="article.url" target="_blank">기사 보기</a>
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
  methods: {
    async searchArticles() {
      const API_KEY = '84fd2c132efdd2f93c83a3ba0735ad7d';
      const url = `https://gnews.io/api/v4/search?q=${encodeURIComponent(this.query)}&lang=ko&token=${API_KEY}`;

      try {
        const response = await fetch(url);
        const data = await response.json();
        this.articles = data.articles;
      } catch (error) {
        console.error('뉴스 검색 실패:', error);
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
.news-card {
  background: #1f2630;
  color: white;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
}
</style>