// =========STOPWATCH FUNCTION - SLEEP COUNTER=========
let isRunning = false;
let startTime, endTime;

const stopwatchBtn = document.getElementById('stopwatchBtn');
const resultDisplay = document.getElementById('resultDisplay');
const timestampDisplay = document.getElementById('timestampDisplay');

// Stopwatch functionality
stopwatchBtn.addEventListener('click', () => {
    if (!isRunning) {
        // START timestamp
        startTime = new Date();
        timestampDisplay.textContent = `Start: ${startTime.toLocaleTimeString()}`;
        stopwatchBtn.textContent = "STOP";
    } else {
        // STOP timestamp
        endTime = new Date();
        const elapsedTime = (endTime - startTime) / 1000; // Convert to seconds
        const hours = Math.floor(elapsedTime / 3600);
        const seconds = Math.floor(elapsedTime % 3600);
        resultDisplay.textContent = `Elapsed Time: ${hours} hrs ${seconds} secs`;
        timestampDisplay.textContent += ` | Stop: ${endTime.toLocaleTimeString()}`;
        stopwatchBtn.textContent = "START";
    }
    isRunning = !isRunning;
});

