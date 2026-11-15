package vn.iotstar.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAConfig {
    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("dbJPA");

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }
}