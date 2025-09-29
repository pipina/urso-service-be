package sk.is.urso.model.urso;

import lombok.Data;
import sk.is.urso.model.urso.SetDlznici;
import java.util.Date;

@Data
public class SetObdobieDto {
    private Long id;
    private Long setDlznici;
    private Date obdobieOd;
    private Date obdobieDo;
    private Short zdroj;
    private String typDlznika;
}
