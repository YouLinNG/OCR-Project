package nju.passport.dao;

import nju.passport.model.Visa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisaDao extends JpaRepository<Visa,String> {
}
