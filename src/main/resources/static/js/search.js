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
          searchResults.innerHTML = '';

          if (data.products.length === 0 && data.categories.length === 0) {
            searchResults.style.display = 'none';
            searchInput.classList.remove('active');
            return;
          }

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
