package hr.algebra.webshop.repository;

import hr.algebra.webshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Username(String username);

    @Query("SELECT o FROM Order o WHERE " +
            "(COALESCE(:username, '') = '' OR o.user.username = :username) AND " +
            "(o.orderDate >= COALESCE(:start, o.orderDate)) AND " +
            "(o.orderDate <= COALESCE(:end, o.orderDate))")
    List<Order> findFiltered(@Param("username") String username,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);
}
