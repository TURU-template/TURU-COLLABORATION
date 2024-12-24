// =========STOPWATCH FUNCTION - SLEEP COUNTER=========

let startTime, endTime;
let run = document.getElementById('stateContainer')
const stopwatchBtn = document.getElementById('stopwatchBtn');
const labelBtn = document.getElementById('labelBtn');
const resultDisplay = document.getElementById('resultDisplay');
const timestampDisplay = document.getElementById('timestampDisplay');

stopwatchBtn.addEventListener('click', () => {
    if (!run) {
        // START timestamp
        startTime = new Date();
        const isoStartTime = startTime.toISOString();
        const startDateStr = `${startTime.getDate().toString().padStart(2, '0')}/${(startTime.getMonth() + 1).toString().padStart(2, '0')}/${startTime.getFullYear().toString().slice(-2)} ${startTime.getHours().toString().padStart(2, '0')}:${startTime.getMinutes().toString().padStart(2, '0')}`;
        timestampDisplay.textContent = `${startDateStr}`;
        stopwatchBtn.textContent = "😴";
        stopwatchBtn.style.backgroundColor = `--lilac`;
        labelBtn.textContent = "Tombol Bangun";
        fetch('/api/add-start', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(isoStartTime)
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Failed to call addStart.');
        })
        .then(data => {
            console.log('Response from addStart:', data);
        })
        .catch(error => console.error('Error:', error));
        

    } else {
        // STOP timestamp
        endTime = new Date();
        const elapsedTime = (endTime - startTime) / 1000; // Convert to seconds
        const hours = Math.floor(elapsedTime / 3600);
        const minutes = Math.floor((elapsedTime % 3600) / 60);

        // Format the elapsed time based on hours
        const elapsedTimeStr = hours > 0 ? `${hours} j ${minutes} m` : `${minutes} m`;
        durationDisplay.textContent = elapsedTimeStr;

        // Format stop timestamp
        const endDateStr = `${endTime.getDate().toString().padStart(2, '0')}/${(endTime.getMonth() + 1).toString().padStart(2, '0')}/${endTime.getFullYear().toString().slice(-2)} ${endTime.getHours().toString().padStart(2, '0')}:${endTime.getMinutes().toString().padStart(2, '0')}`;
        timestampDisplay.textContent += ` - ${endDateStr}`;

        stopwatchBtn.textContent = "🙂";
        labelBtn.textContent = "Tombol Tidur";
        const isoEndTime = endTime.toISOString(); // Convert to ISO format
        
        const requestPayload = {
            endTime: isoEndTime
        }
        fetch('/api/add-end', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestPayload)
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Failed to call addEnd.');
        })
        .then(data => {
            console.log('Response from addEnd:', data);
        })
        .catch(error => console.error('Error:', error));
    }
    run = !run;
});

document.getElementById('tambahModal').addEventListener('shown.bs.modal', function () {
    console.log('Modal berhasil muncul.');
  });
  
// document.addEventListener("DOMContentLoaded", function () {
//     const form = document.getElementById("dataTidurForm");
  
//     form.addEventListener("submit", async (event) => {
//       event.preventDefault();
  
//       const tanggal = document.getElementById("tanggalTidur").value;
//       const waktuMulai = document.getElementById("waktuMulai").value;
//       const waktuSelesai = document.getElementById("waktuSelesai").value;
  
//       // Kombinasi tanggal dan waktu untuk format LocalDateTime
//       const waktuMulaiDateTime = `${tanggal}T${waktuMulai}:00`;
//       const waktuSelesaiDateTime = `${tanggal}T${waktuSelesai}:00`;
  
//       // Ambil pengguna saat ini
//       const response = await fetch("/akun", { method: "GET" });
//       const pengguna = await response.json();
  
//       if (!pengguna || !pengguna.id) {
//         alert("Pengguna tidak ditemukan!");
//         return;
//       }
  
//       // Kirim data ke server
//       const responseTambah = await fetch("/data-tidur/tambah", {
//         method: "POST",
//         headers: {
//           "Content-Type": "application/json",
//         },
//         body: JSON.stringify({
//           waktuMulai: waktuMulaiDateTime,
//           waktuSelesai: waktuSelesaiDateTime,
//           idPengguna: pengguna.id,
//         }),
//       });
  
//       if (responseTambah.ok) {
//         alert("Data tidur berhasil ditambahkan!");
//         location.reload(); // Reload halaman setelah berhasil
//       } else {
//         alert("Terjadi kesalahan saat menambahkan data!");
//       }
//     });
//   });
  
