//아래로 스크롤하면 뉴스

<template>
  <div class="home-layout">
    <div class="home-page">
      <h2>📡 News Feed</h2>

      <!-- 뉴스 그리드 -->
      <div class="news-grid" v-if="paginatedArticles.length">
        <div v-for="article in paginatedArticles" :key="article.url" class="news-card">
          <img :src="article.image" alt="thumbnail" class="news-image" v-if="article.image"/>
          <h3>{{ article.title }}</h3>
          <p>{{ article.description }}</p>
          <a :href="article.url" target="_blank">Read more</a>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="pagination" v-if="allArticles.length">
        <button
            v-for="page in Math.ceil(allArticles.length / pageSize)"
            :key="page"
            @click="changePage(page)"
            :class="{ active: currentPage === page }"
        >
          {{ page }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Home",
  props: {
    keyword: {
      type: String,
      default: '' // 나중에 노드에서 토큰을 받을 예정
    }
  },
  data() {
    return {
      articles: [],
      paginatedArticles: [],
      allArticles: [],
      currentPage: 1,
      pageSize: 10
    };
  },
  mounted() {
    const fixedKeyword = this.keyword?.trim() || "bitcoin";
    console.log("🔍 키워드:", fixedKeyword);
    this.fetchArticles(fixedKeyword);
  },
  methods: {
    async fetchArticles(keyword) {
      try {
        const response = await fetch('http://localhost:8080/api/news', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ keyword })  // 여기서 keyword 전달
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        this.allArticles = (data.articles || []).slice(0, 100); // 최대 100개 제한
        this.updatePaginatedArticles();
      } catch (error) {
        console.error('뉴스 가져오기 실패:', error);
      }
    },
    updatePaginatedArticles() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.paginatedArticles = this.allArticles.slice(start, end);
    },
    changePage(page) {
      this.currentPage = page;
      this.updatePaginatedArticles();
    }
  }
};
</script>

<style scoped>
.home-layout {
  display: flex;
  gap: 24px;
  color: white;
  padding: 24px;
  background: #0d1117;
}

.home-page {
  width: 100%;
}

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

.pagination {
  margin-top: 20px;
  display: flex;
  gap: 8px;
  justify-content: center;
}
.pagination button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  background: #333;
  color: white;
  cursor: pointer;
}
.pagination button.active {
  background: #2196f3;
  font-weight: bold;
}
</style>
