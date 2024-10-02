package com.digibank.agentmanagement.repository.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreenRepository extends JpaRepository<Screen, Long>{

    Optional<Screen> findByName(String name);

    Optional<Screen> findByNameAndScreenIdNot(String name, Long screenId);
}
