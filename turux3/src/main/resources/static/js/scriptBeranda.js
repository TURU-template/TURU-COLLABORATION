let startTime, endTime;
let run =
  document.getElementById("stateContainer").getAttribute("data-state") ===
  "true";

let state = document
  .getElementById("stateContainer")
  .getAttribute("data-state");

const stopwatchBtn = document.getElementById("stopwatchBtn");
const labelBtn = document.getElementById("labelBtn");
const durationDisplay = document.getElementById("durationDisplay");
const timestampDisplay = document.getElementById("timestampDisplay");
let intervalId; // Declare interval ID for duration updates

if (run) {
  labelBtn.textContent = "Tombol Bangun";
  document.getElementById("durationDisplay").classList.add("blinking");
  document.getElementById("mascotNameDisplay").classList.add("blinking");

  // Retrieve and calculate the ongoing duration from the backend-provided start time
  const isoStartTime = document
    .getElementById("stateContainer")
    .getAttribute("data-start-time");
  startTime = new Date(isoStartTime);

  // Calculate and display the elapsed time
  intervalId = setInterval(() => {
    const now = new Date();
    const elapsedTime = (now - startTime) / 1000; // Elapsed time in seconds
    const hours = Math.floor(elapsedTime / 3600);
    const minutes = Math.floor((elapsedTime % 3600) / 60);

    const elapsedTimeStr =
      hours > 0 ? `${hours} jam ${minutes} menit` : `${minutes} menit`;
    durationDisplay.textContent = elapsedTimeStr;
  }, 1000);
} else {
  labelBtn.textContent = "Tombol Tidur";
  document.getElementById("durationDisplay").classList.remove("blinking");
  document.getElementById("mascotNameDisplay").classList.remove("blinking");

  durationDisplay.textContent = ""; // Clear duration if not running
}

// Button click event
stopwatchBtn.addEventListener("click", () => {
  if (!run) {
    // START timestamp
    document.getElementById("durationDisplay").classList.add("blinking");
    document.getElementById("mascotNameDisplay").classList.add("blinking");

    startTime = new Date();
    const isoStartTime = startTime.toISOString();
    const startDateStr = `${startTime.getDate().toString().padStart(2, "0")}/${(
      startTime.getMonth() + 1
    )
      .toString()
      .padStart(2, "0")}/${startTime
      .getFullYear()
      .toString()
      .slice(-2)} ${startTime
      .getHours()
      .toString()
      .padStart(2, "0")}:${startTime.getMinutes().toString().padStart(2, "0")}`;
    timestampDisplay.textContent = `Mulai: ${startDateStr}`;
    stopwatchBtn.textContent = "😴";
    labelBtn.textContent = "Tombol Bangun";

    // Send the start time to the backend
    fetch("/api/add-start", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(isoStartTime),
    })
      .then((response) => {
        if (response.ok) {
          return response.text();
        }
        throw new Error("Failed to call addStart.");
      })
      .then((data) => {
        console.log("Response from addStart:", data);
      })
      .catch((error) => console.error("Error:", error));

    // Start updating the duration every second
    intervalId = setInterval(() => {
      const now = new Date();
      const elapsedTime = (now - startTime) / 1000; // Convert to seconds
      const hours = Math.floor(elapsedTime / 3600);
      const minutes = Math.floor((elapsedTime % 3600) / 60);

      const elapsedTimeStr =
        hours > 0 ? `${hours} j ${minutes} m` : `${minutes} m`;
      durationDisplay.textContent = elapsedTimeStr;
    }, 1000);
  } else {
    // STOP timestamp
    document.getElementById("durationDisplay").classList.remove("blinking");
    document.getElementById("mascotNameDisplay").classList.remove("blinking");

    endTime = new Date();
    clearInterval(intervalId); // Stop the real-time updates

    const elapsedTime = (endTime - startTime) / 1000; // Convert to seconds
    const hours = Math.floor(elapsedTime / 3600);
    const minutes = Math.floor((elapsedTime % 3600) / 60);

    const elapsedTimeStr =
      hours > 0 ? `${hours} j ${minutes} m` : `${minutes} m`;
    durationDisplay.textContent = elapsedTimeStr;

    const endDateStr = `${endTime.getDate().toString().padStart(2, "0")}/${(
      endTime.getMonth() + 1
    )
      .toString()
      .padStart(2, "0")}/${endTime.getFullYear().toString().slice(-2)} ${endTime
      .getHours()
      .toString()
      .padStart(2, "0")}:${endTime.getMinutes().toString().padStart(2, "0")}`;
    timestampDisplay.textContent += ` — ${endDateStr}`;

    stopwatchBtn.textContent = "🙂";
    labelBtn.textContent = "Tombol Tidur";

    // Send the end time to the backend
    const isoEndTime = endTime.toISOString();
    const requestPayload = { endTime: isoEndTime };
    fetch("/api/add-end", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestPayload),
    })
      .then((response) => response.json()) // Expecting a JSON response
      .then((data) => {
        console.log("Response from addEnd:", data);

        // Check if the data was deleted and trigger an alert if so
        if (data.isDeleted) {
          alert("Data ditolak karena durasi tidur dibawah 15 menit");
        }

        // Refresh page
        window.location.reload();
      })
      .catch((error) => console.error("Error:", error));
  }
  run = !run;
});

//limit inputan

document.addEventListener('DOMContentLoaded', function() {
  ['tambahModal', 'editModal'].forEach(modalId => {
    const modal = document.getElementById(modalId);
    if (modal) {
      const waktuMulai = modal.querySelector('[name="waktuMulai"]');
      const waktuSelesai = modal.querySelector('[name="waktuSelesai"]');

      if (waktuMulai && waktuSelesai) {

        setupDateTimeValidation(waktuMulai, waktuSelesai);
      }
    }
  });
});

function setupDateTimeValidation(waktuMulai, waktuSelesai) {
  const now = new Date(); // Waktu sekarang untuk validasi

  waktuMulai.addEventListener('change', function() {
    const startDate = new Date(this.value);

    // Cek jika waktu mulai di masa depan
    if (startDate > now) {
      alert("Waktu mulai tidak boleh di masa depan.");
      this.value = '';
      return;
    }

    // Validasi waktu selesai yang sudah ada
    if (waktuSelesai.value) {
      const endDate = new Date(waktuSelesai.value);

      if (endDate > now) {
        alert("Waktu selesai tidak boleh di masa depan.");
        waktuSelesai.value = '';
        return;
      }

      const maxDate = new Date(startDate);
      maxDate.setHours(maxDate.getHours() + 24);

      if (endDate < startDate || endDate > maxDate) {
        alert("Waktu selesai tidak valid. Harus dalam rentang 24 jam setelah waktu mulai.");
        waktuSelesai.value = '';
        return;
      }

      // Validasi durasi minimal 15 menit
      const duration = endDate - startDate;
      if (duration < 15 * 60 * 1000) { // 15 menit dalam milidetik
        alert("Durasi tidak boleh kurang dari 15 menit.");
        waktuSelesai.value = '';
      }
    }
  });

  waktuSelesai.addEventListener('change', function() {
    if (!waktuMulai.value) {
      alert('Silakan pilih waktu mulai terlebih dahulu');
      this.value = '';
      return;
    }

    const startDate = new Date(waktuMulai.value);
    const endDate = new Date(this.value);

    if (endDate > now) {
      alert("Waktu selesai tidak boleh di masa depan.");
      this.value = '';
      return;
    }

    const maxDate = new Date(startDate);
    maxDate.setHours(maxDate.getHours() + 24);

    if (endDate < startDate) {
      alert('Waktu selesai tidak boleh lebih awal dari waktu mulai.');
      this.value = '';
    } else if (endDate > maxDate) {
      const formattedMax = maxDate.toLocaleString('id-ID', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
      alert(`Waktu selesai tidak boleh lebih dari ${formattedMax}.`);
      this.value = '';
    } else {
      // Validasi durasi minimal 15 menit
      const duration = endDate - startDate;
      if (duration < 15 * 60 * 1000) { // 15 menit dalam milidetik
        alert("Durasi tidak boleh kurang dari 15 menit.");
        this.value = '';
      }
    }
  });

  // Trigger validasi awal jika input sudah memiliki nilai
  if (waktuMulai.value) {
    waktuMulai.dispatchEvent(new Event('change'));
  }

  if (waktuSelesai.value) {
    waktuSelesai.dispatchEvent(new Event('change'));
  }
}
