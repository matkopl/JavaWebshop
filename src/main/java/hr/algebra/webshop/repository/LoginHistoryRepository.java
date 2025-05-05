package hr.algebra.webshop.repository;

import hr.algebra.webshop.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    @Query("SELECT lh FROM LoginHistory lh WHERE " +
            "(COALESCE(:username, '') = '' OR lh.username = :username) AND " +
            "(lh.loginTime >= COALESCE(:start, lh.loginTime)) AND " +
            "(lh.loginTime <= COALESCE(:end, lh.loginTime))")
    List<LoginHistory> findFiltered(@Param("username") String username, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}