package com.prompter.repository;


import com.prompter.domain.Site;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findByUrl(String url);
}
