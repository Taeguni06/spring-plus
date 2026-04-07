package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    // 1. 날씨와 기간 모두 있는 경우
    @Query("SELECT t FROM Todo t WHERE t.weather = :weather AND t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findAllByWeatherAndModifiedAtBetween(String weather, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 2. 날씨만 있는 경우
    @Query("SELECT t FROM Todo t WHERE t.weather = :weather")
    Page<Todo> findAllByWeather(String weather, Pageable pageable);

    // 3. 기간만 있는 경우
    @Query("SELECT t FROM Todo t WHERE t.modifiedAt BETWEEN :start AND :end")
    Page<Todo> findAllByModifiedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
