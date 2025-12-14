package org.j2os.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.j2os.entity.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountClassicService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Account account){
        entityManager.persist(account);
    }

    public List<Account> getAccounts(){
        return entityManager
                .createQuery("select o from Account o")
                .getResultList();
    }
}
