package hr.algebra.webshop.repository;

import hr.algebra.webshop.model.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedIpRepository extends JpaRepository<BlockedIp, Long> {
    boolean existsByIpAddress(String ipAddress);
}
