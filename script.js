window.onload = function() {
    const leftCurtain = document.getElementById('curtain').querySelector('.left-curtain');
    const rightCurtain = document.getElementById('curtain').querySelector('.right-curtain');
    const titlePage = document.getElementById('titlepage');
    const aboutSection = document.querySelector('.content');
    const curtain = document.getElementById('curtain');

    // Scroll to the top of the page
    window.scrollTo(0, 0);

    // Initially hide vertical overflow
    document.body.style.overflowY = 'hidden';
    document.documentElement.style.overflowY = 'hidden';
    
    setTimeout(() => {
        leftCurtain.style.transform = 'translateX(-100%) scaleX(0.1)';
        rightCurtain.style.transform = 'translateX(100%) scaleX(0.1)';
    }, 1000);

    setTimeout(() => {
        
        titlePage.classList.add('animate-title');
    }, 2500); // Delay before the title animation starts

    setTimeout(() => {
        curtain.style.display = 'none'; // Hide the curtain so title page is selectable
        aboutSection.classList.add('fade-in');
        document.body.style.overflowY = 'auto';
        document.documentElement.style.overflowY = 'auto';
    }, 4500); // Delay before the about section fades in

};