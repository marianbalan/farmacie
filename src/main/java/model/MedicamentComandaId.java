package model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MedicamentComandaId implements Serializable {

    @Column(name = "idComanda")
    private Integer idComanda;

    @Column(name = "idMedicament")
    private Integer idMedicament;

    public MedicamentComandaId() {
    }

    public MedicamentComandaId(Integer idComanda, Integer idMedicament) {
        this.idComanda = idComanda;
        this.idMedicament = idMedicament;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        MedicamentComandaId that = (MedicamentComandaId) o;
        return Objects.equals(idComanda, that.idComanda) &&
                Objects.equals(idMedicament, that.idMedicament);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idComanda, idMedicament);
    }

}
