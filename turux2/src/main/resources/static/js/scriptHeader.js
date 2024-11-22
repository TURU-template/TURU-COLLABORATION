//document.getElementById('navbar-container').innerHTML = fetch('navbar.html').then(response => response.text());

// =========DATE TIME HEADER=========
const currentDateDisplay = document.getElementById('currentDate');
const currentTimeDisplay = document.getElementById('currentTime');

// Function to update date and time
function updateDateTime() {
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    currentDateDisplay.textContent = now.toLocaleDateString('id-ID', options);
    currentTimeDisplay.textContent = now.toLocaleTimeString('en-US', { hour12: true });
}
// Update the date and time every second
setInterval(updateDateTime, 1000);