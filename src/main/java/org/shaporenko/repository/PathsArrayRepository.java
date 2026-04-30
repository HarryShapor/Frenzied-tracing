package org.shaporenko.repository;


import org.shaporenko.entity.PathsArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathsArrayRepository extends JpaRepository<PathsArray, Long> {



}
