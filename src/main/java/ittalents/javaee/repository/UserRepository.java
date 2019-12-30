package ittalents.javaee.repository;

import ittalents.javaee.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    @Override
    List<User> findAll();

    @Override
    Optional<User> findById(Long id);

    @Override
    <S extends User> S save(S s);

    @Override
    void delete(User user);

    @Override
    boolean existsById(Long id);

    @Override
    void deleteById(Long id);
}
