// Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// State
let authToken = null;
let currentUser = null;
let cart = null;
let products = [];

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    // Check if user is already logged in
    authToken = localStorage.getItem('authToken');
    if (authToken) {
        loadUserProfile();
    }
});

// Authentication Functions
function showTab(tab) {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.add('hidden');

    if (tab === 'login') {
        document.getElementById('loginForm').classList.remove('hidden');
    } else {
        document.getElementById('registerForm').classList.remove('hidden');
    }

    // Update active tab
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    event.target.classList.add('active');
}

async function login(event) {
    event.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            throw new Error('Invalid credentials');
        }

        const data = await response.json();
        authToken = data.token;
        currentUser = data;
        localStorage.setItem('authToken', authToken);

        showAlert('Login successful!', 'success');
        showApp();
    } catch (error) {
        showAlert('Login failed: ' + error.message, 'error');
    }
}

async function register(event) {
    event.preventDefault();

    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const firstName = document.getElementById('regFirstName').value;
    const lastName = document.getElementById('regLastName').value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password, firstName, lastName })
        });

        if (!response.ok) {
            throw new Error('Registration failed');
        }

        const data = await response.json();
        authToken = data.token;
        currentUser = data;
        localStorage.setItem('authToken', authToken);

        showAlert('Registration successful!', 'success');
        showApp();
    } catch (error) {
        showAlert('Registration failed: ' + error.message, 'error');
    }
}

async function loadUserProfile() {
    try {
        const response = await fetch(`${API_BASE_URL}/users/profile`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load profile');
        }

        currentUser = await response.json();
        showApp();
    } catch (error) {
        localStorage.removeItem('authToken');
        authToken = null;
    }
}

function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');

    document.getElementById('authSection').classList.remove('hidden');
    document.getElementById('appSection').classList.add('hidden');
    document.getElementById('userInfo').classList.add('hidden');

    showAlert('Logged out successfully', 'success');
}

function showApp() {
    document.getElementById('authSection').classList.add('hidden');
    document.getElementById('appSection').classList.remove('hidden');
    document.getElementById('userInfo').classList.remove('hidden');
    document.getElementById('userName').textContent = `${currentUser.firstName} ${currentUser.lastName}`;

    loadProducts();
    loadCart();
}

// Navigation
function showSection(section) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.add('hidden'));
    document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));

    document.getElementById(`${section}Section`).classList.remove('hidden');
    event.target.classList.add('active');

    if (section === 'products') {
        loadProducts();
    } else if (section === 'cart') {
        loadCart();
    } else if (section === 'orders') {
        loadOrders();
    }
}

// Products Functions
async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load products');
        }

        const data = await response.json();
        products = data.content || data;
        displayProducts(products);
    } catch (error) {
        showAlert('Failed to load products: ' + error.message, 'error');
    }
}

function displayProducts(productsToDisplay) {
    const productsList = document.getElementById('productsList');

    if (productsToDisplay.length === 0) {
        productsList.innerHTML = '<p class="loading">No products found</p>';
        return;
    }

    productsList.innerHTML = productsToDisplay.map(product => `
        <div class="product-card">
            <h3>${product.name}</h3>
            <p class="price">$${product.price}</p>
            <p class="description">${product.description || ''}</p>
            <div class="quantity-control">
                <label>Qty:</label>
                <input type="number" id="qty-${product.id}" value="1" min="1" max="10">
            </div>
            <button onclick="addToCart(${product.id})" class="btn btn-success">Add to Cart</button>
        </div>
    `).join('');
}

async function searchProducts() {
    const query = document.getElementById('searchInput').value;

    if (!query) {
        displayProducts(products);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/products/search?query=${query}`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error('Search failed');
        }

        const results = await response.json();
        displayProducts(results);
    } catch (error) {
        showAlert('Search failed: ' + error.message, 'error');
    }
}

// Cart Functions
async function addToCart(productId) {
    const product = products.find(p => p.id === productId);
    const quantity = parseInt(document.getElementById(`qty-${productId}`).value);

    try {
        const response = await fetch(`${API_BASE_URL}/cart/add`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                productId: product.id,
                productName: product.name,
                price: product.price,
                quantity: quantity
            })
        });

        if (!response.ok) {
            throw new Error('Failed to add to cart');
        }

        cart = await response.json();
        updateCartCount();
        showAlert('Added to cart!', 'success');
    } catch (error) {
        showAlert('Failed to add to cart: ' + error.message, 'error');
    }
}

async function loadCart() {
    try {
        const response = await fetch(`${API_BASE_URL}/cart`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load cart');
        }

        cart = await response.json();
        displayCart();
        updateCartCount();
    } catch (error) {
        showAlert('Failed to load cart: ' + error.message, 'error');
    }
}

function displayCart() {
    const cartItems = document.getElementById('cartItems');
    const cartTotal = document.getElementById('cartTotal');

    if (!cart || !cart.items || cart.items.length === 0) {
        cartItems.innerHTML = '<p class="loading">Your cart is empty</p>';
        cartTotal.innerHTML = '';
        return;
    }

    cartItems.innerHTML = cart.items.map(item => `
        <div class="cart-item">
            <div class="cart-item-info">
                <h4>${item.productName}</h4>
                <p class="price">$${item.price} x ${item.quantity} = $${item.subtotal}</p>
            </div>
            <button onclick="removeFromCart(${item.productId})" class="btn btn-secondary btn-sm">Remove</button>
        </div>
    `).join('');

    const total = cart.items.reduce((sum, item) => sum + parseFloat(item.subtotal), 0);
    cartTotal.innerHTML = `<h3>Total: $${total.toFixed(2)}</h3>`;
}

async function removeFromCart(productId) {
    try {
        const response = await fetch(`${API_BASE_URL}/cart/remove/${productId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to remove from cart');
        }

        cart = await response.json();
        displayCart();
        updateCartCount();
        showAlert('Removed from cart', 'success');
    } catch (error) {
        showAlert('Failed to remove from cart: ' + error.message, 'error');
    }
}

function updateCartCount() {
    const count = cart && cart.items ? cart.items.length : 0;
    document.getElementById('cartCount').textContent = count;
}

// Order Functions
async function placeOrder() {
    if (!cart || !cart.items || cart.items.length === 0) {
        showAlert('Your cart is empty', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/orders`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to place order');
        }

        const order = await response.json();
        showAlert(`Order placed successfully! Order ID: ${order.id}`, 'success');

        // Clear cart and reload
        cart = null;
        updateCartCount();
        displayCart();

        // Switch to orders tab
        showSection('orders');
        document.querySelector('.nav-tab:nth-child(3)').click();
    } catch (error) {
        showAlert('Failed to place order: ' + error.message, 'error');
    }
}

async function loadOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/orders`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load orders');
        }

        const orders = await response.json();
        displayOrders(orders);
    } catch (error) {
        showAlert('Failed to load orders: ' + error.message, 'error');
    }
}

function displayOrders(orders) {
    const ordersList = document.getElementById('ordersList');

    if (!orders || orders.length === 0) {
        ordersList.innerHTML = '<p class="loading">No orders yet</p>';
        return;
    }

    ordersList.innerHTML = orders.map(order => `
        <div class="order-card">
            <div class="order-header">
                <div>
                    <h3>Order #${order.id}</h3>
                    <p>${new Date(order.createdAt).toLocaleString()}</p>
                </div>
                <span class="order-status ${order.status.toLowerCase()}">${order.status}</span>
            </div>
            <div class="order-items">
                ${order.items.map(item => `
                    <div class="order-item">
                        <strong>${item.productName}</strong> -
                        Qty: ${item.quantity} -
                        $${item.subtotal}
                    </div>
                `).join('')}
            </div>
            <div style="margin-top: 15px; text-align: right;">
                <strong>Total: $${order.totalAmount}</strong>
            </div>
        </div>
    `).join('');
}

// Alert Function
function showAlert(message, type) {
    const alert = document.getElementById('alert');
    alert.textContent = message;
    alert.className = `alert ${type}`;
    alert.classList.remove('hidden');

    setTimeout(() => {
        alert.classList.add('hidden');
    }, 3000);
}
