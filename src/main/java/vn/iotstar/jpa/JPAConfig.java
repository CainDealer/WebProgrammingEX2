package vn.iotstar.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAConfig {
    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("dbJPA");

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }
}