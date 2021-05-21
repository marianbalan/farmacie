//package model;
//
//
//import javax.persistence.*;
//import javax.persistence.Entity;
//import java.util.Objects;
//
//@Entity(name = "MedicamentComanda")
//@Table(name="MedicamentComanda")
//public class MedicamentComanda {
//
//    @EmbeddedId
//    private MedicamentComandaId id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("idComanda")
//    private Comanda comanda;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("idMedicament")
//    private Medicament medicament;
//
//    private Integer cantitate;
//
//    public MedicamentComanda(Comanda comanda, Medicament medicament, Integer cantitate) {
//        this.comanda = comanda;
//        this.medicament = medicament;
//        this.cantitate = cantitate;
//        this.id = new MedicamentComandaId(comanda.getId(), medicament.getId());
//    }
//
//    public MedicamentComanda() {}
//
//
//    public Comanda getComanda() {
//        return comanda;
//    }
//
//    public void setComanda(Comanda comanda) {
//        this.comanda = comanda;
//    }
//
//
//    public Medicament getMedicament() {
//        return medicament;
//    }
//
//    public void setMedicament(Medicament medicament) {
//        this.medicament = medicament;
//    }
//
//    public Integer getCantitate() {
//        return cantitate;
//    }
//
//    public void setCantitate(Integer cantitate) {
//        this.cantitate = cantitate;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//
//        if (o == null || getClass() != o.getClass())
//            return false;
//
//        MedicamentComanda that = (MedicamentComanda) o;
//        return Objects.equals(comanda, that.comanda) &&
//                Objects.equals(medicament, that.medicament);
//    }
//
//    public String getNume() {
//        return medicament.getNume();
//    }
//
//    public String getProducator() {
//        return medicament.getProducator();
//    }
//
//    public TipMedicament getTip() {
//        return medicament.getTip();
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(comanda, medicament);
//    }
//
//    @Override
//    public String toString() {
//        return "MedicamentComanda{" +
//                ", comanda=" + comanda +
//                ", medicament=" + medicament +
//                ", cantitate=" + cantitate +
//                '}';
//    }
//}

package model;

import java.util.Objects;

public class MedicamentComanda extends Entity<Tuple<Integer,Integer>> {

    private Integer cantitate;
    private Medicament medicament;

    public MedicamentComanda(Integer cantitate) {
        this.cantitate = cantitate;
    }

    public Integer getCantitate() {
        return cantitate;
    }

    public void setCantitate(Integer cantitate) {
        this.cantitate = cantitate;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }

    public String getNume() {
        return medicament.getNume();
    }

    public String getProducator() {
        return medicament.getProducator();
    }

    public TipMedicament getTip() {
        return medicament.getTip();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicamentComanda tag = (MedicamentComanda) o;
        return Objects.equals(getId().getLeft(), tag.getId().getLeft())
                && Objects.equals(getId().getRight(), tag.getId().getRight());
    }
}
