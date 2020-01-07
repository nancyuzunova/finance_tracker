package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndPassword(String email, String password);

    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    //to send mails when user has not been active for
    List<User> findAllByLastLoginBefore(LocalDate date);
}
