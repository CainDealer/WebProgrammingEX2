package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import vn.iotstar.dao.UserDao;
import vn.iotstar.jpa.JPAConfig;
import vn.iotstar.model.User;

public class UserDaoImpl implements UserDao {

    @Override
    public User get(String username) {
        return findOne("userName", username);
    }

    @Override
    public User getByEmail(String email) {
        return findOne("email", email);
    }

    @Override
    public User getByRememberToken(String hashedToken) {
        return findOne("rememberToken", hashedToken);
    }

    @Override
    public void insert(User user) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(user);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) trans.rollback();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(User user) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(user);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) trans.rollback();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean checkExistEmail(String email) {
        return count("email", email) > 0;
    }

    @Override
    public boolean checkExistUsername(String username) {
        return count("userName", username) > 0;
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return count("phone", phone) > 0;
    }

    @Override
    public User getByPhone(String phone) {
        return findOne("phone", phone);
    }

    private User findOne(String field, String value) {
        if (value == null) return null;
        EntityManager em = JPAConfig.getEntityManager();
        try {
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u WHERE u." + field + " = :value", User.class);
            q.setParameter("value", value);
            q.setMaxResults(1);
            List<User> list = q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    private long count(String field, String value) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u." + field + " = :value", Long.class);
            q.setParameter("value", value);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }
}