package repository;

import model.Farmacist;
import model.Medic;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Query;

public class BDMedicRepository implements MedicRepository{
    SessionFactory sessionFactory;

    public BDMedicRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Medic findByUserAndPassword(String username, String password) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = null;
            try{
                transaction = session.beginTransaction();
                Query query = session.createQuery("from Medic as f  where f.username =:username and f.password =:password");
                query.setParameter("username", username);
                query.setParameter("password", password);
                Medic medic = (Medic)query.getSingleResult();

                transaction.commit();
                System.out.println("MEDIC: " + medic.getUsername());
                return medic;
            }
            catch(Exception e){
                if(transaction != null)
                    transaction.rollback();
                return null;
            }
        }
    }

    @Override
    public Medic findOne(Integer integer) {
        return null;
    }

    @Override
    public Iterable<Medic> findAll() {
        return null;
    }

    @Override
    public void save(Medic entity) {

    }
}
