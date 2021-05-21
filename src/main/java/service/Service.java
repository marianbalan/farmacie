package service;

import model.*;
import model.validators.ValidationException;
import model.validators.Validator;
import repository.*;
import utils.events.ComandaChangeEvent;
import utils.events.Event;
import utils.events.EventType;
import utils.observer.Observable;
import utils.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Service  implements Observable {

    private FarmacistRepository farmacistRepository;
    private MedicRepository medicRepository;
    private ComandaRepository comandaRepository;
    private MedicamentRepository medicamentRepository;
    private MedicamentComandaRepository medicamentComandaRepository;
    private Validator<Medicament> medicamentValidator;
    private Validator<Comanda> comandaValidator;


    public Service(FarmacistRepository farmacistRepository, MedicRepository medicRepository, ComandaRepository comandaRepository, MedicamentRepository medicamentRepository, MedicamentComandaRepository medicamentComandaRepository, Validator<Medicament> medicamentValidator, Validator<Comanda> comandaValidator) {
        this.farmacistRepository = farmacistRepository;
        this.medicRepository = medicRepository;
        this.comandaRepository = comandaRepository;
        this.medicamentRepository = medicamentRepository;
        this.medicamentComandaRepository = medicamentComandaRepository;
        this.medicamentValidator = medicamentValidator;
        this.comandaValidator = comandaValidator;
    }

    public HashMap<String, Object> findUser(String username, String password) throws ServicesException {
        User user = farmacistRepository.findByUserAndPassword(username, password);
        Rol rol = Rol.farmacist;
        if (user == null) {
            user = medicRepository.findByUserAndPassword(username, password);
            rol = Rol.medic;
        }
        if (user == null) {
            throw new ServicesException("Invalid username or password!");
        }
        User finalUser = user;
        Rol finalRol = rol;
        return new HashMap<String, Object>(){{
            put("user", finalUser);
            put("rol", finalRol);
        }};
    }

    public Iterable<Comanda> findAllOrders() throws ServicesException{
        return comandaRepository.findAll();
    }

    public Iterable<Comanda> findMyOrders(Medic medic) throws ServicesException {
        return comandaRepository.findMyOrders(medic.getUsername());
    }

    public Iterable<Medicament> findAllMedicamente() throws ServicesException {
        return medicamentRepository.findAll();
    }

    public Medicament saveMedicament(Medicament medicament) throws ServicesException {
        try{
            medicamentValidator.validate(medicament);
            medicamentRepository.save(medicament);
        }
        catch (ValidationException ex){
            throw new ServicesException(ex.getMessage());
        }
        return medicament;
    }

    public List<MedicamentComanda> findMedicamenteByComanda(Comanda comanda) {
        return medicamentComandaRepository.getMedicamenteComandaByIdComanda(comanda.getId());
    }

    public void anuleazaComanda(Comanda comanda) throws ServicesException {
        if (!comanda.getStatus().equals(TipStatus.pending))
            throw new ValidationException("Comanda selectata nu este in procesare!");
        comandaRepository.update(comanda.getId(), TipStatus.canceled);
        notifyObservers(new ComandaChangeEvent(EventType.ANULEAZA, null));
    }

    public List<Medicament> findMedicamenteByNume(String nume) {
        return medicamentRepository.findByName(nume);
    }

    public void addComanda(LocalDate data, TipStatus status, String medicUsername, List<MedicamentComanda> medicamenteComanda) throws ServicesException {
        for (MedicamentComanda mc : medicamenteComanda){
            if(mc.getCantitate() > mc.getMedicament().getCantitateTotala()){
                throw new ServicesException("Cantitatea aleasa este prea mare!");
            }
        }
        Comanda comanda = new Comanda(medicUsername, data, status, medicamenteComanda);
        try{
            comandaValidator.validate(comanda);
            comandaRepository.save(comanda);
            Integer idComanda = comandaRepository.getMaxId();
            for (MedicamentComanda mc : medicamenteComanda){
                Tuple<Integer, Integer> id = new Tuple<Integer, Integer>(idComanda, mc.getMedicament().getId());
                mc.setId(id);
                medicamentComandaRepository.save(mc);
            }
            notifyObservers(new ComandaChangeEvent(EventType.EFECTUEAZA, null));
        }
        catch(ValidationException ex){
            throw new ServicesException(ex.getMessage());
        }
    }

    public void acceptaComanda(Comanda comanda) throws ServicesException {
        if (!comanda.getStatus().equals(TipStatus.pending))
            throw new ValidationException("Comanda selectata nu este in procesare!");
        for (MedicamentComanda mc : comanda.getMedicamente())
            if (mc.getMedicament().getCantitateTotala() < mc.getCantitate())
                throw new ValidationException("Stoc indisponibil!");

        comandaRepository.update(comanda.getId(), TipStatus.approved);
        for(MedicamentComanda mc : comanda.getMedicamente()){
            int cantitate = mc.getMedicament().getCantitateTotala() - mc.getCantitate();
            medicamentRepository.update(mc.getMedicament().getId(), cantitate);
            notifyObservers(new ComandaChangeEvent(EventType.ACCEPTA, null));
        }
    }

    public void refuzaComanda(Comanda comanda) throws ServicesException {
        if (!comanda.getStatus().equals(TipStatus.pending))
            throw new ValidationException("Comanda selectata nu este in procesare!");
        try{
            comandaRepository.update(comanda.getId(), TipStatus.rejected);
            notifyObservers(new ComandaChangeEvent(EventType.REFUZA, null));
        }
        catch(ValidationException ex){
            throw new ServicesException("Eroare la refuzarea comenzii" + ex);
        }
    }

    private List<Observer> observers = new ArrayList<Observer>();

    @Override
    public void addObserver(Observer e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(Event t) {
        observers.forEach(x -> x.update(t));
    }
}
