<template>
  <div class="arkham-app">
    <!-- 상단 네비게이션 바 -->
    <header class="arkham-header">
      <div class="left-section">
        <div class="logo" @click="goHome">
          <span class="logo-text">TimeStamper</span>
        </div>
        <nav class="nav-links">
          <router-link to="/charts" class="nav-button">Charts</router-link>
          <router-link to="/visual" class="nav-button">Visual</router-link>
          <router-link to="/heatmap" class="nav-button">Heatmap</router-link>

          <div class="dropdown">
            <button class="nav-button" @click="toggleTop3">TOP 3 Address</button>
            <div v-if="showTop3" class="dropdown-content">
              <button class="top-button">TOP1</button>
              <button class="top-button">TOP2</button>
              <button class="top-button">TOP3</button>
            </div>
          </div>
        </nav>
      </div>
    </header>
    <main class="arkham-content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showTop3: false
    };
  },
  methods: {
    goHome() {
      this.$router.push("/");
    },
    toggleTop3() {
      this.showTop3 = !this.showTop3;
    }
  }
};
</script>

<style scoped>
.arkham-app {
  min-height: 100vh;
  background-color: #0B0E14;
  color: white;
  font-family: 'Roboto Mono', monospace;
  display: flex;
  flex-direction: column;
}

.nav-button {
  padding: 10px 20px;
  border-radius: 8px;
  background: #1f2630;
  color: #c7d2fe;
  text-decoration: none;
  transition: 0.2s;
  font-weight: 500;
  font-size: 16px;
  cursor: pointer;
  border: none;
}

.nav-button:hover {
  background: #2196f3;
  color: white;
}

.arkham-header {
  display: flex;
  align-items: center;
  background-color: #12161D;
  padding: 16px 30px;
  border-bottom: 1px solid #1f1f1f;
  box-shadow: 0 0 6px #1f1f1f;
  position: sticky;
  top: 0;
  z-index: 1000;
  font-size: 18px;
}

.left-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  cursor: pointer;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #00bfff;
  letter-spacing: 1px;
}

.nav-links {
  display: flex;
  gap: 16px;
  align-items: center;
}

.nav-button {
  padding: 10px 20px;
  border-radius: 8px;
  background: #1f2630;
  color: #c7d2fe;
  text-decoration: none;
  transition: 0.2s;
  font-weight: 500;
  font-size: 16px;
  cursor: pointer;
  border: none;
}

.nav-button:hover {
  background: #2196f3;
  color: white;
}

.nav-button.disabled {
  pointer-events: none;
  opacity: 0.5;
}

.dropdown {
  position: relative;
}

.dropdown-content {
  position: absolute;
  top: 110%;
  left: 0;
  background: rgba(50, 50, 50, 0.85);
  backdrop-filter: blur(8px);
  border-radius: 8px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 100;
}

.top-button {
  background-color: #1f2630;
  color: #93c5fd;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 15px;
}

.top-button:hover {
  background-color: #2196f3;
  color: white;
}

.arkham-content {
  flex-grow: 1;
  padding: 24px;
}
</style>
