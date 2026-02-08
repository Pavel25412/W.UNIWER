/* global gsap, ScrollTrigger */

document.addEventListener('DOMContentLoaded', () => {
  // Проверка безопасности: если GSAP не загрузился, ничего не делаем
  if (typeof gsap === 'undefined') {
    console.warn('GSAP не найден. Анимации выключены.');
    return;
  }

  gsap.registerPlugin(ScrollTrigger);

  // --- Анимация Хедера ---
  // Мы используем .from(), то есть берем ВИДИМЫЙ элемент и говорим
  // "прилети сюда из прозрачности". Это безопаснее.
  gsap.from('nav', { y: -50, opacity: 0, duration: 1 });
  gsap.from('#main-logo', {
    scale: 0,
    rotation: -180,
    duration: 0.8,
    delay: 0.2,
  });

  // --- Анимация Главного текста ---
  gsap.from('.animate-me', {
    y: 50,
    opacity: 0,
    duration: 1,
    stagger: 0.2, // По очереди
    ease: 'power2.out',
  });

  // --- Анимация Карточек при скролле ---
  gsap.from('.animate-scroll', {
    scrollTrigger: {
      trigger: '.about',
      start: 'top 80%', // Начинаем, когда блок виден
    },
    y: 50,
    opacity: 0,
    duration: 0.8,
    stagger: 0.2,
  });
});
