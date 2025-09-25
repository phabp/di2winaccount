package br.com.di2win.account.repositories;

import br.com.di2win.account.entities.Account;
import br.com.di2win.account.entities.Operation;
import br.com.di2win.account.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    /** Extrato paginado (ordenado DESC por data) para statement detalhado */
    @Query("""
      select o
      from Operation o
      where o.account = :account
        and o.operationDate between :start and :end
      order by o.operationDate desc
    """)
    Page<Operation> statement(
        @Param("account") Account account,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );

    /** Soma de um tipo (ex.: WITHDRAW) no período informado */
    @Query("""
      select coalesce(sum(o.value), 0)
      from Operation o
      where o.account = :account
        and o.type = :type
        and o.operationDate between :start and :end
    """)
    BigDecimal sumByPeriod(
        @Param("account") Account account,
        @Param("type") OperationType type,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /** Extrato simples entre instantes, ordenado ASC (para o menu) */
    List<Operation> findByAccountAndOperationDateBetweenOrderByOperationDateAsc(
        Account account,
        LocalDateTime start,
        LocalDateTime end
    );
}
