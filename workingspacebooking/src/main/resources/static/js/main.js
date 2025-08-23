/* ===== Admin Interface JavaScript =====
 * - Language switcher functionality
 * - Sidebar navigation highlighting
 * - DOM ready initialization
 */

document.addEventListener("DOMContentLoaded", function () {
    initializeLanguageSwitcher();
    initializeSidebarNavigation();
});

// ===== Language Switcher Functions =====

/**
 * Thay đổi ngôn ngữ của trang
 * @param {string} lang - Mã ngôn ngữ ('vi' hoặc 'en')
 */
function changeLanguage(lang) {
    const url = new URL(window.location);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}

/**
 * Khởi tạo highlighting cho language switcher
 */
function initializeLanguageSwitcher() {
    const currentLang = getCurrentLanguage();
    highlightCurrentLanguage(currentLang);
}

/**
 * Lấy ngôn ngữ hiện tại từ URL parameter hoặc default
 * @returns {string} Mã ngôn ngữ hiện tại
 */
function getCurrentLanguage() {
    const urlParams = new URLSearchParams(window.location.search);
    const langFromUrl = urlParams.get('lang');
    
    if (langFromUrl) {
        return langFromUrl;
    }
    
    // Check từ document.documentElement.lang
    const htmlLang = document.documentElement.lang;
    if (htmlLang) {
        return htmlLang.toLowerCase().split('-')[0]; // 'vi-VN' -> 'vi'
    }
    
    return 'vi'; // Default fallback
}

/**
 * Highlight nút ngôn ngữ hiện tại
 * @param {string} currentLang - Ngôn ngữ hiện tại
 */
function highlightCurrentLanguage(currentLang) {
    const languageItems = document.querySelectorAll('.language-item');
    
    // Remove existing active classes
    languageItems.forEach(item => {
        item.classList.remove('active', 'text-white', 'bg-primary');
    });
    
    // Add active class to current language
    const currentLanguageItem = document.querySelector(`[data-lang="${currentLang}"]`);
    if (currentLanguageItem) {
        currentLanguageItem.classList.add('active', 'text-white', 'bg-primary');
    }
}

// ===== Sidebar Navigation Functions =====

/**
 * Khởi tạo highlighting cho sidebar navigation
 */
function initializeSidebarNavigation() {
    const currentPath = window.location.pathname;
    const sidebarItems = document.querySelectorAll('.sidebar-nav-item');
    
    // Remove all active classes first
    sidebarItems.forEach(item => {
        item.classList.remove('active');
    });
    
    // Add active class to current page
    sidebarItems.forEach(item => {
        const navType = item.getAttribute('data-nav');
        if (isCurrentPage(currentPath, navType)) {
            item.classList.add('active');
        }
    });
}

/**
 * Kiểm tra xem có phải trang hiện tại không
 * @param {string} currentPath - Đường dẫn hiện tại
 * @param {string} navType - Loại navigation (dashboard, users, venues, notifications)
 * @returns {boolean} True nếu là trang hiện tại
 */
function isCurrentPage(currentPath, navType) {
    // Normalize path - remove trailing slash
    const path = currentPath.endsWith('/') && currentPath.length > 1 
        ? currentPath.slice(0, -1) 
        : currentPath;
    
    switch (navType) {
        case 'dashboard':
            return path === '/admin' || path === '/admin/dashboard';
        case 'users':
            return path.startsWith('/admin/users');
        case 'notifications':
            return path.startsWith('/admin/notifications');
        case 'venues':
            return path.startsWith('/admin/venues');
        default:
            return false;
    }
}
