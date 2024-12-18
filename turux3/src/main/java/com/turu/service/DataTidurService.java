import com.turu.model.DataTidur;
import com.turu.repository.DataTidurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class DataTidurService {
    @Autowired
    private DataTidurRepository dataTidurRepository;

    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate) {
        return dataTidurRepository.findByTanggalBetween(startDate, endDate);
    }
}
