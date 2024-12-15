//document.getElementById('navbar-container').innerHTML = fetch('navbar.html').then(response => response.text());

// =========DATE TIME HEADER=========
const currentDateDisplay = document.getElementById('currentDate');
const currentTimeDisplay = document.getElementById('currentTime');

// Function to update date and time
function updateDateTime() {
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    currentDateDisplay.textContent = now.toLocaleDateString('id-ID', options);
    currentTimeDisplay.textContent = now.toLocaleTimeString('id-ID', { hour12: false });
}
// Update the date and time every second
setInterval(updateDateTime, 1000);


// ========= HIASAN =========
function generateRandomDots() {
    const colors = ['#2B194F', '#514FC2', '#35A4DA', '#8C4FC2', '#DA5798'];
    const backdrop = document.body; // Target backdrop
    const dotCount = 120; // Adjust as needed
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    for (let i = 0; i < dotCount; i++) {
        const dot = document.createElement('div');
        dot.className = 'dot';
        dot.style.width = `${Math.random() * 5 + 4}px`; // Random size
        dot.style.height = dot.style.width;
        dot.style.backgroundColor = colors[Math.floor(Math.random() * colors.length)];
        dot.style.top = `${Math.random() * viewportHeight}px`; // Confined to viewport height
        dot.style.left = `${Math.random() * viewportWidth}px`; // Confined to viewport width
        backdrop.appendChild(dot);
    }
}

generateRandomDots();

