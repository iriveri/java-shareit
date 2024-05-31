package ru.practicum.shareit.user.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;

@Repository
@Qualifier("DatabaseUserStorage")
public class DatabaseUserStorage implements UserStorage {
    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean contains(Long userId) {
        return em.find(User.class, userId) != null;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            em.remove(user);
        }
    }

    @Override
    public User fetchUser(Long userId) {
        return em.find(User.class, userId);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, User updatedUser) {
        User user = em.find(User.class, userId);
        if (user != null) {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            em.merge(user);
        }
    }

    @Override
    @Transactional
    public Long addUser(User user) {
        em.persist(user);
        return user.getId();
    }

    @Override
    public Collection<User> fetchAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }
}
