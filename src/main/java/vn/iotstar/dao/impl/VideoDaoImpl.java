package vn.iotstar.dao.impl;

import java.util.List;
import java.util.function.Consumer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.iotstar.dao.VideoDao;
import vn.iotstar.jpa.JPAConfig;
import vn.iotstar.model.User;
import vn.iotstar.model.Video;

public class VideoDaoImpl implements VideoDao {

    private void inTransaction(Consumer<EntityManager> work) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction t = em.getTransaction();
        try {
            t.begin();
            work.accept(em);
            t.commit();
        } catch (RuntimeException e) {
            if (t.isActive()) t.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void insert(Video video) {
        inTransaction(em -> {
            video.setUser(em.getReference(User.class, video.getUser().getId()));
            em.persist(video);
        });
    }

    @Override
    public void update(Video video) {
        inTransaction(em -> em.merge(video));
    }

    @Override
    public void delete(int id) {
        inTransaction(em -> {
            Video v = em.find(Video.class, id);
            if (v != null) em.remove(v);
        });
    }

    @Override
    public Video findById(int id) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            List<Video> list = em.createQuery(
                    "SELECT v FROM Video v JOIN FETCH v.user WHERE v.id = :id", Video.class)
                    .setParameter("id", id).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video> findLatest(int limit) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT v FROM Video v JOIN FETCH v.user WHERE v.active = true "
                    + "ORDER BY v.createdDate DESC, v.id DESC", Video.class)
                    .setMaxResults(limit).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Video> findByUser(int userId, int page, int size) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT v FROM Video v JOIN FETCH v.user WHERE v.user.id = :uid "
                    + "ORDER BY v.createdDate DESC, v.id DESC", Video.class)
                    .setParameter("uid", userId)
                    .setFirstResult((page - 1) * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByUser(int userId) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(v) FROM Video v WHERE v.user.id = :uid", Long.class)
                    .setParameter("uid", userId).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void increaseViews(int id) {
        inTransaction(em -> em.createQuery("UPDATE Video v SET v.views = v.views + 1 WHERE v.id = :id")
                .setParameter("id", id).executeUpdate());
    }
}