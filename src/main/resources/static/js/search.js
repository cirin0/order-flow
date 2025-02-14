document.addEventListener('DOMContentLoaded', function () {
  const searchInput = document.querySelector('.search-input');
  const searchResults = document.createElement('div');
  searchResults.className = 'search-results';
  document.querySelector('.search-container').appendChild(searchResults);

  let timeoutId;

  searchInput.addEventListener('input', function () {
    clearTimeout(timeoutId);
    const query = this.value.trim();

    if (query.length < 2) {
      searchResults.style.display = 'none';
      searchInput.classList.remove('active');
      return;
    }

    timeoutId = setTimeout(() => {
      fetch(`/api/search?query=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
          searchResults.innerHTML = ''; // Очистити попередні результати

          // Перевірка, чи є результати
          if (data.products.length === 0 && data.categories.length === 0) {
            // Вивести повідомлення "Товарів не знайдено"
            const noResultsMessage = document.createElement('div');
            noResultsMessage.className = 'no-results-message';
            noResultsMessage.textContent = 'Товарів не знайдено';
            searchResults.appendChild(noResultsMessage);
            searchResults.style.display = 'block';
            searchInput.classList.add('active');
            return;
          }

          // Додати результати пошуку
          if (data.products.length > 0) {
            const productsSection = document.createElement('div');
            productsSection.className = 'search-section';
            productsSection.innerHTML = '<div class="search-section-title">Продукти</div>';

            data.products.forEach(product => {
              const item = document.createElement('a');
              item.href = `/products/${product.id}`;
              item.className = 'search-item';
              item.innerHTML = `
                <div class="search-item-image">
                  <img src="${product.imageUrl || '/images/placeholder.png'}" alt="${product.name}">
                </div>
                <div class="search-item-info">
                  <div class="search-item-name">${product.name}</div>
                  <div class="search-item-price">${product.price} грн</div>
                </div>
              `;
              productsSection.appendChild(item);
            });
            searchResults.appendChild(productsSection);
          }

          if (data.categories.length > 0) {
            const categoriesSection = document.createElement('div');
            categoriesSection.className = 'search-section';
            categoriesSection.innerHTML = '<div class="search-section-title">Категорії</div>';

            data.categories.forEach(category => {
              const item = document.createElement('a');
              item.href = `/categories/${category.id}/products`;
              item.className = 'search-item';
              item.innerHTML = `
                <div class="search-item-image">
                  <img src="${category.imageUrl || '/images/placeholder.png'}" alt="${category.name}">
                </div>
                <div class="search-item-info">
                  <div class="search-item-name">${category.name}</div>
                </div>
              `;
              categoriesSection.appendChild(item);
            });
            searchResults.appendChild(categoriesSection);
          }

          searchResults.style.display = 'block';
          searchInput.classList.add('active');
        })
        .catch(error => {
          console.error('Помилка пошуку:', error);
          // Вивести повідомлення про помилку
          const errorMessage = document.createElement('div');
          errorMessage.className = 'error-message';
          errorMessage.textContent = 'Помилка завантаження результатів';
          searchResults.appendChild(errorMessage);
          searchResults.style.display = 'block';
          searchInput.classList.add('active');
        });
    }, 300);
  });

  document.addEventListener('click', function (e) {
    if (!e.target.closest('.search-container')) {
      searchResults.style.display = 'none';
      searchInput.classList.remove('active');
    }
  });
});
