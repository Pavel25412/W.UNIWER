import '/style.css';

document.addEventListener('DOMContentLoaded', () => {
  gsap.registerPlugin(ScrollTrigger);

  // --- 1. PRELOADER (ФЛАГ) ---
  const tl = gsap.timeline();

  // Сброс позиций для анимации
  gsap.set('.flag-top', { y: '-100%' });
  gsap.set('.flag-bottom', { y: '100%' });

  tl.to('.flag-top', { y: '0%', duration: 1, ease: 'power4.inOut' })
    .to('.flag-bottom', { y: '0%', duration: 1, ease: 'power4.inOut' }, '<') // '<' значит одновременно
    .to('.preloader-text', { opacity: 1, duration: 0.5 }) // Показываем текст
    .to('.preloader-text', { opacity: 0, duration: 0.5, delay: 0.8 }) // Прячем текст
    .to('.flag-top', { y: '-100%', duration: 1, ease: 'power2.in' }) // Открываем шторки
    .to('.flag-bottom', { y: '100%', duration: 1, ease: 'power2.in' }, '<')
    .to('.preloader', { display: 'none' }); // Убираем блок

  // --- 2. HERO Анимация (Запускается после прелоадера) ---
  tl.from(
    '.hero-title .line',
    {
      y: 100,
      opacity: 0,
      stagger: 0.1,
      duration: 1,
      ease: 'power3.out',
    },
    '-=0.5',

  // --- 3. MARQUEE (Бегущая строка) ---
  gsap.to('.marquee-content', {
    xPercent: -50,
    ease: 'none',
    duration: 20,
    repeat: -1, // Бесконечно
  });

  // --- 4. ГОРИЗОНТАЛЬНЫЙ СКРОЛЛ (ВУЗы) ---
  const uniContainer = document.querySelector('.uni-container');

  let scrollAmount = uniContainer.offsetWidth - window.innerWidth;

  gsap.to(uniContainer, {
    x: () => -(uniContainer.scrollWidth - window.innerWidth + 50), // +50 отступ
    ease: 'none',
    scrollTrigger: {
      trigger: '.universities-section',
      start: 'top top',
      end: () => '+=' + uniContainer.scrollWidth,
      scrub: 1,
      pin: true,
      invalidateOnRefresh: true,
    },
  });



  // --- 5. BENTO GRID (Появление) ---
  gsap.from('.bento-item', {
    scrollTrigger: {
      trigger: '.bento-grid',
      start: 'top 80%',
    },
    y: 100,
    opacity: 0,
    stagger: 0.1,
    duration: 0.8,
  });
});
