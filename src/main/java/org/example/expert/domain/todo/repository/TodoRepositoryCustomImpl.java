package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user, user).fetchJoin() // Fetch Join으로 User를 한 번에 조회
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(String title, LocalDateTime start, LocalDateTime end, String nickname, Pageable pageable) {

        List<TodoSearchResponse> content = queryFactory
                .select(new QTodoSearchResponse(
                        todo.title,
                        manager.countDistinct(), // 담당자 수
                        comment.countDistinct()  // 댓글 수
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(manager.user, user) // 닉네임 검색용 조인
                .where(
                        titleContains(title),
                        nicknameContains(nickname),
                        createdAtBetween(start, end)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc()) // 생성일 최신순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 쿼리 (페이징용)
        long total = Optional.ofNullable(
                queryFactory
                        .select(todo.count())
                        .from(todo)
                        .where(
                                titleContains(title),
                                nicknameContains(nickname),
                                createdAtBetween(start, end)
                        )
                        .fetchOne()
        ).orElse(0L); // 결과가 null이면 0L을 반환

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? todo.title.contains(title) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickname.contains(nickname) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return null;
        if (start == null) return todo.createdAt.loe(end);
        if (end == null) return todo.createdAt.goe(start);
        return todo.createdAt.between(start, end);
    }
}

