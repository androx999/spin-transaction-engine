package io.javabrains.transaccionesspinprueba.infrastructure.persistence.specification;

import io.javabrains.transaccionesspinprueba.domain.enums.TransactionStatus;
import io.javabrains.transaccionesspinprueba.domain.enums.TransactionType;
import io.javabrains.transaccionesspinprueba.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.domain.Specification;

public final class TransactionSpecification {

    private TransactionSpecification(){

    }

    public static Specification<TransactionEntity> hasAccountId(String accountId){
        return(root,query,criteriaBuilder) -> {
            if(accountId == null || accountId.isBlank()){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("accountId"),accountId);
        };
    }

    public static Specification<TransactionEntity> hasStatus(TransactionStatus status){
        return (root, query, criteriaBuilder) -> {
            if(status == null){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"),status);
        };
    }

    public static Specification<TransactionEntity> hasType(TransactionType type)
    {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {

                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("type"),type);
        };
    }
}
